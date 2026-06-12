package sc.laplace.test.job;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.nebula.connection.NebulaClientOptions;
import org.apache.flink.connector.nebula.connection.NebulaGraphConnectionProvider;
import org.apache.flink.connector.nebula.connection.NebulaMetaConnectionProvider;
import org.apache.flink.connector.nebula.sink.NebulaEdgeBatchOutputFormat;
import org.apache.flink.connector.nebula.sink.NebulaSinkFunction;
import org.apache.flink.connector.nebula.sink.NebulaVertexBatchOutputFormat;
import org.apache.flink.connector.nebula.statement.EdgeExecutionOptions;
import org.apache.flink.connector.nebula.statement.VertexExecutionOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import sc.laplace.test.config.AppConfig;
import sc.laplace.test.mapper.EdgeRowMapper;
import sc.laplace.test.mapper.FileEdgeRowMapper;
import sc.laplace.test.mapper.HashRowMapper;
import sc.laplace.test.mapper.VertexRowMapper;
import sc.laplace.test.model.Record;
import sc.laplace.test.util.StreamingJobUtils;

import java.util.Collections;
import java.util.List;

public final class NebulaSinkBuilder {
    private NebulaSinkBuilder() {
    }

    /**
     * 注册普通顶点 sink。
     * 输入 Record 会先映射成 Nebula connector 约定的 Row，再按 tag 写入。
     */
    public static void addVertexSink(DataStream<Record> stream, AppConfig config, NebulaClientOptions clientOptions,
                                     String tag, List<String> fields, List<Integer> positions) {
        // Nebula connector 通过 positions 从 Row 中取值，0 位固定是 VID，属性位从 1 开始。
        VertexExecutionOptions executionOptions = new VertexExecutionOptions.ExecutionOptionBuilder()
                .setGraphSpace(config.getNebulaSpace())
                .setTag(tag)
                .setIdIndex(0)
                .setFields(fields)
                .setPositions(positions)
                .setBatchSize(config.getNebulaBatchSize())
                .setBatchIntervalMs(config.getNebulaBatchIntervalMs())
                .build();

        stream.map(new VertexRowMapper(fields))
                .addSink(new NebulaSinkFunction<>(new NebulaVertexBatchOutputFormat(
                        new NebulaGraphConnectionProvider(clientOptions),
                        new NebulaMetaConnectionProvider(clientOptions),
                        executionOptions)))
                .name("VertexSink-" + tag);
    }

    /**
     * 为 file 的各类 hash 建独立 hash 顶点，后续 file_* 边可以通过 hash_of_file 反查 file VID。
     */
    public static void addHashVertexSink(DataStream<Record> stream, AppConfig config, NebulaClientOptions clientOptions, String hashField) {
        // hash 顶点的 VID 直接使用 hash 值的 sha256，避免 Nebula VID 里出现超长原始字符串。
        VertexExecutionOptions executionOptions = new VertexExecutionOptions.ExecutionOptionBuilder()
                .setGraphSpace(config.getNebulaSpace())
                .setTag(hashField)
                .setIdIndex(0)
                .setFields(Collections.singletonList(hashField))
                .setPositions(StreamingJobUtils.SINGLE_VALUE_POSITION)
                .setBatchSize(config.getNebulaBatchSize())
                .setBatchIntervalMs(config.getNebulaBatchIntervalMs())
                .build();

        stream.flatMap(new HashRowMapper(hashField, HashRowMapper.OutputMode.HASH_VERTEX))
                .addSink(new NebulaSinkFunction<>(new NebulaVertexBatchOutputFormat(
                        new NebulaGraphConnectionProvider(clientOptions),
                        new NebulaMetaConnectionProvider(clientOptions),
                        executionOptions)))
                .name("VertexSink-" + hashField);
    }

    /**
     * 写入 hash -> file 的索引边。
     */
    public static void addHashEdgeSink(DataStream<Record> stream, AppConfig config, NebulaClientOptions clientOptions,
                                       String edgeName, String hashField) {
        addEdgeFlatMapSink(stream, config, clientOptions, edgeName,
                new HashRowMapper(hashField, HashRowMapper.OutputMode.HASH_EDGE),
                false, "EdgeSink-" + edgeName + "-" + hashField);
    }

    /**
     * 注册普通一对一边 sink。
     */
    public static void addEdgeSink(DataStream<Record> stream, AppConfig config, NebulaClientOptions clientOptions, String edgeName) {
        addEdgeMapSink(stream, config, clientOptions, edgeName, new EdgeRowMapper(), true, "EdgeSink-" + edgeName);
    }

    /**
     * 注册 file 相关特殊边 sink。
     * 这类边需要先从 hash 回查真实 file VID，再展开成最终边。
     */
    public static void addFileEdgeSink(DataStream<Record> stream, AppConfig config, NebulaClientOptions clientOptions, String edgeName) {
        // file 相关边需要拆成多条真实边，因此使用 flatMap 而不是一对一 map。
        addEdgeFlatMapSink(stream, config, clientOptions, edgeName,
                new FileEdgeRowMapper(config, resolveFileEdgeMode(edgeName)),
                true, "EdgeSink-" + edgeName);
    }

    private static void addEdgeMapSink(DataStream<Record> stream, AppConfig config, NebulaClientOptions clientOptions,
                                       String edgeName, MapFunction<Record, Row> mapper, boolean withRank, String sinkName) {
        EdgeExecutionOptions executionOptions = createEdgeExecutionOptions(config, edgeName, withRank);
        // 普通边是一条输入记录对应一条 Nebula 边，直接走 map 即可。
        stream.map(mapper)
                .addSink(new NebulaSinkFunction<>(new NebulaEdgeBatchOutputFormat(
                        new NebulaGraphConnectionProvider(clientOptions),
                        new NebulaMetaConnectionProvider(clientOptions),
                        executionOptions)))
                .name(sinkName);
    }

    private static void addEdgeFlatMapSink(DataStream<Record> stream, AppConfig config, NebulaClientOptions clientOptions,
                                           String edgeName, FlatMapFunction<Record, Row> mapper, boolean withRank, String sinkName) {
        EdgeExecutionOptions executionOptions = createEdgeExecutionOptions(config, edgeName, withRank);
        // hash_of_file / file_* 这类边可能一条输入展开成多条输出，需要 flatMap。
        stream.flatMap(mapper)
                .addSink(new NebulaSinkFunction<>(new NebulaEdgeBatchOutputFormat(
                        new NebulaGraphConnectionProvider(clientOptions),
                        new NebulaMetaConnectionProvider(clientOptions),
                        executionOptions)))
                .name(sinkName);
    }

    private static EdgeExecutionOptions createEdgeExecutionOptions(AppConfig config, String edgeName, boolean withRank) {
        EdgeExecutionOptions.ExecutionOptionBuilder builder = new EdgeExecutionOptions.ExecutionOptionBuilder()
                .setGraphSpace(config.getNebulaSpace())
                .setEdge(edgeName)
                .setSrcIndex(0)
                .setDstIndex(1)
                // 目前所有边都只写 src/dst/rank，不携带额外属性。
                .setFields(Collections.emptyList())
                .setPositions(Collections.emptyList())
                .setBatchSize(config.getNebulaBatchSize())
                .setBatchIntervalMs(config.getNebulaBatchIntervalMs());
        if (withRank) {
            builder.setRankIndex(2);
        }
        return builder.build();
    }

    private static FileEdgeRowMapper.FileEdgeMode resolveFileEdgeMode(String edgeName) {
        if (edgeName != null) {
            String normalized = edgeName.trim().toLowerCase();
            if ("ip_download_file".equals(normalized) || "domain_download_file".equals(normalized)) {
                // download 类边语义是实体指向文件，和 file_* 默认方向相反。
                return FileEdgeRowMapper.FileEdgeMode.ENTITY_TO_FILE;
            }
        }
        return FileEdgeRowMapper.FileEdgeMode.FILE_TO_ENTITY;
    }
}
