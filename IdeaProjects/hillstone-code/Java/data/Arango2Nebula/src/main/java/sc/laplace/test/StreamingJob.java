/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sc.laplace.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.connector.nebula.connection.NebulaClientOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import sc.laplace.test.config.AppConfig;
import sc.laplace.test.constant.RecordType;
import sc.laplace.test.job.NebulaSinkBuilder;
import sc.laplace.test.job.VertexMappings;
import sc.laplace.test.model.Record;
import sc.laplace.test.source.ArangoSource;
import sc.laplace.test.util.IocValidators;
import sc.laplace.test.util.StreamingJobUtils;

import java.util.List;

/**
 * Flink 一次性迁移任务入口。
 * 负责把 ArangoSource 产出的统一 Record 流拆成点/边两条链路，
 * 再按 Nebula tag / edge type 注册对应 sink。
 *
 * <p>整体执行顺序是：
 * 1. 从 application.properties 读取迁移参数；
 * 2. 启动 Arango source，先读取 vertex collection，再读取 edge collection；
 * 3. 对 vertex / edge 分流并按 collection 过滤；
 * 4. 写入 Nebula，并处理 file/hash 这类特殊建图逻辑。
 */
@Slf4j
public class StreamingJob {

    public static void main(String[] args) throws Exception {
        // 整个任务是一次性迁移作业：从 Arango 按批读取，再按点/边分别写入 Nebula。
        AppConfig config = AppConfig.load();

        log.info("Starting ArangoDB to NebulaGraph One-Time Migration. config: {}", config);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(config.getParallelism());
        if (config.getFlinkCheckpointIntervalMs() > 0) {
            env.enableCheckpointing(config.getFlinkCheckpointIntervalMs());
        }

        DataStream<Record> source = env.addSource(new ArangoSource(config));
        // 顶层 source 同时产出点和边，先按 RecordType 拆流，后续 sink 才能使用各自专属 mapper。
        DataStream<Record> vertexSource = source.filter(filterByType(RecordType.VERTEX)).name("VertexSource");
        DataStream<Record> edgeSource = source.filter(filterByType(RecordType.EDGE)).name("EdgeSource");

        NebulaClientOptions clientOptions = new NebulaClientOptions.NebulaClientOptionsBuilder()
                .setGraphAddress(config.getNebulaHosts())
                .setMetaAddress(config.getNebulaMetaHosts())
                .setUsername(config.getNebulaUser())
                .setPassword(config.getNebulaPassword())
                .build();

        registerVertexSinks(vertexSource, config, clientOptions);
        registerEdgeSinks(edgeSource, config, clientOptions);

        env.execute("ArangoDB to NebulaGraph One-Time Migration");
    }

    private static void registerVertexSinks(DataStream<Record> vertexSource, AppConfig config, NebulaClientOptions clientOptions) {
        for (String nebulaTag : config.getNebulaTags()) {
            // 同一个 Arango collection 可能需要落成多个 Nebula tag，例如 ipv4/domain/file 都会先写入公共 ioc tag。
            DataStream<Record> collectionStream = vertexSource
                    .filter(filterByCollection(nebulaTag))
                    .filter(filterValidVertex(nebulaTag))
                    .name("VertexCollection-" + nebulaTag);

            List<VertexMappings.VertexSinkDefinition> definitions = VertexMappings.getDefinitions(nebulaTag);
            if (definitions == null) {
                log.warn("Unknown vertex collection: {}", nebulaTag);
                continue;
            }

            for (VertexMappings.VertexSinkDefinition definition : definitions) {
                NebulaSinkBuilder.addVertexSink(collectionStream, config, clientOptions,
                        definition.getTag(), definition.getFields(), definition.getPositions());
            }

            if ("file".equalsIgnoreCase(nebulaTag)) {
                // file 顶点额外拆出 hash 顶点和 hash_of_file 边，供后续 file_* 关系通过 hash 反查真实 file VID。
                for (String hashField : StreamingJobUtils.HASH_FIELDS) {
                    NebulaSinkBuilder.addHashVertexSink(collectionStream, config, clientOptions, hashField);
                    NebulaSinkBuilder.addHashEdgeSink(collectionStream, config, clientOptions, "hash_of_file", hashField);
                }
            }
        }
    }

    private static void registerEdgeSinks(DataStream<Record> edgeSource, AppConfig config, NebulaClientOptions clientOptions) {
        for (String nebulaEdge : config.getNebulaEdges()) {
            // 每个 edge collection 独立下沉，便于按 Nebula edge type 控制写入逻辑。
            DataStream<Record> collectionStream = edgeSource
                    .filter(filterByCollection(nebulaEdge))
                    .name("EdgeCollection-" + nebulaEdge);

            if (isFileEdge(nebulaEdge)) {
                // file 相关边在源数据里通常只带 hash，需要先把 hash 映射回已落库的 file 顶点。
                NebulaSinkBuilder.addFileEdgeSink(collectionStream, config, clientOptions, nebulaEdge);
                continue;
            }

            NebulaSinkBuilder.addEdgeSink(collectionStream, config, clientOptions, nebulaEdge);
        }
    }

    private static boolean isFileEdge(String edgeName) {
        if (edgeName == null) {
            return false;
        }
        String normalized = edgeName.trim().toLowerCase();
        return normalized.startsWith("file_")
                || "ip_download_file".equals(normalized)
                || "domain_download_file".equals(normalized);
    }

    private static FilterFunction<Record> filterByType(final RecordType type) {
        return new FilterFunction<Record>() {
            @Override
            public boolean filter(Record value) {
                // 这里顺手兜底过滤掉 payload 为空的脏数据，避免 mapper/sink 再做空指针防御。
                return value != null
                        && value.getType() == type
                        && ((type == RecordType.VERTEX && value.getVertex() != null)
                        || (type == RecordType.EDGE && value.getEdge() != null));
            }
        };
    }

    private static FilterFunction<Record> filterByCollection(final String collection) {
        return new FilterFunction<Record>() {
            @Override
            public boolean filter(Record value) {
                return value != null
                        && value.getCollection() != null
                        && value.getCollection().equalsIgnoreCase(collection);
            }
        };
    }

    private static FilterFunction<Record> filterValidVertex(final String collection) {
        return new FilterFunction<Record>() {
            @Override
            public boolean filter(Record value) {
                return value != null && IocValidators.isValidVertex(collection, value.getVertex());
            }
        };
    }
}
