package sc.laplace.test.source;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.model.AqlQueryOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
/**
 * 执行 Arango AQL 分页查询，并把分页游标与处理进度持续回写到 OffsetStateStore。
 *
 * <p>这里不直接暴露“整条查询一次读完”的能力，而是强制按 batch 拉取。
 * 这样做可以控制单次查询规模，并把重复消费范围压缩到当前 batch。
 */
public class ArangoQueryExecutor {
    private final int maxRetries;
    private final long retryDelayMs;
    private final int batchSize;
    private final OffsetStateStore offsetStateStore;
    private final ArangoSource.RunningFlag runningFlag;

    public ArangoQueryExecutor(int maxRetries, long retryDelayMs, int batchSize,
                               OffsetStateStore offsetStateStore, ArangoSource.RunningFlag runningFlag) {
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
        this.batchSize = batchSize;
        this.offsetStateStore = offsetStateStore;
        this.runningFlag = runningFlag;
    }

    public <T> void executeQueryWithRetry(ArangoDatabase db, AqlQueryOptions options, Class<T> clazz,
                                          QueryProcessor<T> processor, String stateKey, String rawQuery) {
        int limit = Math.max(1, batchSize);
        int processedCount = offsetStateStore.getProcessedCount(stateKey);
        String cursor = offsetStateStore.getCursor(stateKey);

        while (runningFlag.isRunning()) {
            // 每次查询只拉一个 batch，并在 batch 结束后推进 offset，避免长事务占用 Arango cursor。
            BatchResult batchResult = executePagedQueryWithRetry(db, rawQuery, options, clazz, processor, stateKey, processedCount, cursor, limit);
            int fetched = batchResult.getFetched();
            if (fetched <= 0) {
                if (fetched < 0) {
                    offsetStateStore.save(true);
                }
                return;
            }
            processedCount += fetched;
            cursor = batchResult.getLastCursor();
            // 先更新内存态，再按节流策略落盘；这样即使高频刷批次也不会每条都写文件。
            offsetStateStore.updateProgress(stateKey, processedCount, cursor);
            if (fetched < limit) {
                offsetStateStore.save(true);
                return;
            }
            offsetStateStore.save(false);
        }
    }

    private <T> BatchResult executePagedQueryWithRetry(ArangoDatabase db, String query, AqlQueryOptions options,
                                               Class<T> clazz, QueryProcessor<T> processor,
                                               String stateKey, int processedCount, String cursor, int limit) {
        int attempt = 0;
        while (attempt < maxRetries && runningFlag.isRunning()) {
            Map<String, Object> bindVars = new HashMap<String, Object>();
            // 统一约定 query 只依赖 cursor + limit 两个参数，所有枚举里的 AQL 都必须遵守这个分页协议。
            bindVars.put("cursor", cursor);
            bindVars.put("limit", limit);

            try (ArangoCursor<T> queryCursor = db.query(query, bindVars, options, clazz)) {
                int count = 0;
                String lastCursor = null;
                for (T item : queryCursor) {
                    ProcessResult processResult = processor.process(item);
                    if (!processResult.isContinueProcessing()) {
                        // 返回 -1 表示外部主动停止，保留当前 cursor 供 cancel/close 时强制刷盘。
                        return new BatchResult(-1, lastCursor);
                    }
                    count++;
                    lastCursor = processResult.getCursor();
                    // 单条推进进度，确保异常中断时最多重复当前 batch 内少量数据。
                    offsetStateStore.updateProgress(stateKey, processedCount + count, lastCursor);
                }
                return new BatchResult(count, lastCursor);
            } catch (Exception e) {
                attempt++;
                log.warn("Query execution failed (attempt {}/{}, query: {}): {}",
                        attempt, maxRetries, query, e.getMessage());
                if (attempt >= maxRetries) {
                    log.error("Query failed after {} attempts: {}", maxRetries, query);
                    throw new RuntimeException("Query execution failed after retries", e);
                }
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
        return new BatchResult(0, cursor);
    }

    /**
     * 调用方提供的单条记录处理器。
     * 返回值同时携带“是否继续处理”和“当前记录对应的下一游标”。
     */
    public interface QueryProcessor<T> {
        ProcessResult process(T item);
    }

    /**
     * 单条记录处理后的控制结果。
     */
    public static class ProcessResult {
        private final boolean continueProcessing;
        private final String cursor;

        public ProcessResult(boolean continueProcessing, String cursor) {
            this.continueProcessing = continueProcessing;
            this.cursor = cursor;
        }

        public boolean isContinueProcessing() {
            return continueProcessing;
        }

        public String getCursor() {
            return cursor;
        }
    }

    /**
     * 单批查询结果。
     * fetched 小于 0 表示外部主动停止，不代表真正读到了负数条记录。
     */
    private static class BatchResult {
        private final int fetched;
        private final String lastCursor;

        private BatchResult(int fetched, String lastCursor) {
            this.fetched = fetched;
            this.lastCursor = lastCursor;
        }

        public int getFetched() {
            return fetched;
        }

        public String getLastCursor() {
            return lastCursor;
        }
    }
}
