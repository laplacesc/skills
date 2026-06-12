package sc.laplace.test.source;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.model.AqlQueryOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import sc.laplace.test.config.AppConfig;
import sc.laplace.test.constant.EdgeType;
import sc.laplace.test.constant.RecordType;
import sc.laplace.test.constant.VertexType;
import sc.laplace.test.model.Record;
import sc.laplace.test.model.edge.Edge;
import sc.laplace.test.model.vertex.Vertex;
import sc.laplace.test.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ArangoDB 自定义 Flink Source。
 *
 * <p>职责分为三层：
 * 1. 管理 Arango 连接、重试和生命周期；
 * 2. 以“先点后边”的 phase 顺序调度 collection 抓取；
 * 3. 结合 OffsetStateStore 维护分页游标，实现一次性迁移的断点续跑。
 */
@Slf4j
@RequiredArgsConstructor
public class ArangoSource extends RichSourceFunction<Record> {
    // source 级别重试用于覆盖整个 phase 的失败，例如 Arango 连接中断或某个 collection 查询异常。
    static final int MAX_RETRIES = 3;
    static final long RETRY_DELAY_MS = 5000;
    static final long OFFSET_FLUSH_INTERVAL_MS = 1000;
    private final AppConfig config;
    private transient ArangoDB arangoDB;
    private volatile boolean running = true;
    private transient OffsetStateStore offsetStateStore;
    private transient PhaseRunner phaseRunner;
    private transient ArangoQueryExecutor queryExecutor;
    private transient RunningFlag runningFlag;

    @Override
    public void open(Configuration parameters) {
        arangoDB = createArangoDBInstance();
        // offset 文件同时记录已处理条数和最后一个游标，用于一次性迁移失败后从断点续跑。
        offsetStateStore = new OffsetStateStore(config.getArangoOffsetStateFile(), OFFSET_FLUSH_INTERVAL_MS);
        offsetStateStore.load();
        // collection 之间可以并行抓取，但单个 collection 的数据顺序仍由查询中的 cursor 保证。
        phaseRunner = new PhaseRunner(config.getParallelism());
        runningFlag = new RunningFlag() {
            @Override
            public boolean isRunning() {
                return running;
            }
        };
        queryExecutor = new ArangoQueryExecutor(MAX_RETRIES, RETRY_DELAY_MS,
                config.getArangoCursorBatchSize(), offsetStateStore, runningFlag);
    }

    private ArangoDB createArangoDBInstance() {
        ArangoDB.Builder builder = new ArangoDB.Builder()
                .user(config.getArangoUser())
                .password(config.getArangoPassword())
                // Query time can exceed 30s on large edge scans; keep it configurable.
                .timeout(config.getArangoTimeoutMs())
                // 连接存活时间
                .connectionTtl(60000L)
                // 最大连接数
                .maxConnections(8)
                // 启用主机列表获取
                .acquireHostList(true);

        for (AppConfig.ArangoEndpoint endpoint : config.getArangoHosts()) {
            builder.host(endpoint.getHost(), endpoint.getPort());
        }

        return builder.build();
    }

    @Override
    public void run(SourceContext<Record> ctx) throws IOException {
        int retryCount = 0;

        while (running && retryCount <= MAX_RETRIES) {
            try {
                // 先迁移点再迁移边，避免 Nebula 中边先到达时找不到目标 VID。
                processCollections(ctx, config.getNebulaTags(), "vertex", new CollectionResolver() {
                    @Override
                    public CollectionDefinition resolve(String collection) {
                        VertexType vertexType = VertexType.getByCollection(collection);
                        return vertexType == null ? null : new CollectionDefinition(collection, vertexType.getClazz(), vertexType.getQuery(), RecordType.VERTEX);
                    }
                });
                processCollections(ctx, config.getNebulaEdges(), "edge", new CollectionResolver() {
                    @Override
                    public CollectionDefinition resolve(String collection) {
                        EdgeType edgeType = EdgeType.getByCollection(collection);
                        return edgeType == null ? null : new CollectionDefinition(collection, edgeType.getClazz(), edgeType.getQuery(), RecordType.EDGE);
                    }
                });
                flushOffsetState();
                break; // 成功完成则退出循环
            } catch (Exception e) {
                log.error("ArangoDB connection error (attempt {}/{})", retryCount + 1, MAX_RETRIES, e);

                if (!running) {
                    break;
                }

                if (retryCount == MAX_RETRIES) {
                    log.error("Max retries reached, stopping source");
                    throw new RuntimeException("Failed to connect to ArangoDB after " + MAX_RETRIES + " retries", e);
                }

                // 关闭现有连接并重试
                if (arangoDB != null) {
                    arangoDB.shutdown();
                }

                retryCount++;
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                    // 重建连接
                    arangoDB = createArangoDBInstance();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted during retry delay", ie);
                }
            }
        }
    }

    private void processCollections(SourceContext<Record> ctx, List<String> collections, String phaseName, final CollectionResolver resolver) {
        // 一个 phase 内可以多 collection 并行，但 phase 之间仍保持“先点后边”的依赖顺序。
        phaseRunner.run(collections, phaseName, new PhaseRunner.CollectionProcessor() {
            @Override
            public void process(String collection, SourceContext<Record> ctx, Object checkpointLock) {
                CollectionDefinition definition = resolver.resolve(collection);
                if (definition == null) {
                    log.error("Unknown {} collection: {}", phaseName, collection);
                    return;
                }
                queryExecutor.executeQueryWithRetry(createDatabase(), createQueryOptions(), String.class, new ArangoQueryExecutor.QueryProcessor<String>() {
                    @Override
                    public ArangoQueryExecutor.ProcessResult process(String json) {
                        String cursor = extractCursor(json);
                        if (!running) {
                            return new ArangoQueryExecutor.ProcessResult(false, cursor);
                        }
                        // 每条记录先反序列化成具体 vertex/edge 模型，再交给下游统一 sink 链路处理。
                        Object payload = JsonUtil.toObject(json, definition.getClazz());
                        if (payload == null) {
                            return new ArangoQueryExecutor.ProcessResult(true, cursor);
                        }
                        synchronized (checkpointLock) {
                            // Flink Source 必须在 checkpoint lock 内发射元素，确保断点信息与输出一致。
                            ctx.collect(definition.toRecord(payload));
                        }
                        return new ArangoQueryExecutor.ProcessResult(true, cursor);
                    }
                }, definition.getStateKey(), definition.getQuery());
            }
        }, runningFlag, ctx);
    }

    private ArangoDatabase createDatabase() {
        return arangoDB.db(config.getArangoDatabase());
    }

    private AqlQueryOptions createQueryOptions() {
        return new AqlQueryOptions()
                .batchSize(config.getArangoCursorBatchSize());
    }

    private String extractCursor(String json) {
        Map<String, Object> payload = JsonUtil.toMap(json);
        // AQL 查询会把排序字段别名成 __cursor，用它作为下一批查询的游标。
        Object cursor = payload.get("__cursor");
        return cursor == null ? null : cursor.toString();
    }

    @Override
    public void cancel() {
        running = false;
        flushOffsetState();
    }

    @Override
    public void close() {
        flushOffsetState();
        if (arangoDB != null) {
            try {
                arangoDB.shutdown();
            } catch (Exception e) {
                log.warn("Error shutting down ArangoDB connection", e);
            }
        }
    }

    private void flushOffsetState() {
        if (offsetStateStore != null) {
            offsetStateStore.save(true);
        }
    }

    /**
     * 把运行状态抽成接口，便于 PhaseRunner / QueryExecutor 在不直接依赖 Source 的情况下感知 cancel。
     */
    interface RunningFlag {
        boolean isRunning();
    }

    /**
     * 按 collection 名解析出查询模板、模型类型和输出 RecordType。
     */
    private interface CollectionResolver {
        CollectionDefinition resolve(String collection);
    }

    /**
     * 单个 collection 的运行时定义。
     * 它把“配置里的一项字符串”补全成 source 真正执行查询和组装 Record 所需的全部信息。
     */
    private static final class CollectionDefinition {
        private final String collection;
        private final Class<?> clazz;
        private final String query;
        private final RecordType recordType;

        private CollectionDefinition(String collection, Class<?> clazz, String query, RecordType recordType) {
            this.collection = collection;
            this.clazz = clazz;
            this.query = query;
            this.recordType = recordType;
        }

        private Class<?> getClazz() {
            return clazz;
        }

        private String getQuery() {
            return query;
        }

        private String getStateKey() {
            // stateKey 维度精确到 phase + collection，避免不同查询链路共用同一个 offset。
            return recordType.name().toLowerCase() + ":" + collection;
        }

        private Record toRecord(Object payload) {
            if (recordType == RecordType.VERTEX) {
                return Record.vertex(collection, (Vertex) payload);
            }
            return Record.edge(collection, (Edge) payload);
        }
    }
}
