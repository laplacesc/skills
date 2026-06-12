package sc.laplace.test.mapper;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import sc.laplace.test.model.Record;
import sc.laplace.test.model.vertex.Vertex;
import sc.laplace.test.util.IocValidators;
import sc.laplace.test.util.VidHelper;

public class HashRowMapper implements FlatMapFunction<Record, Row> {
    private final String hashField;
    private final VertexRowMapper.FieldExtractor hashExtractor;
    private final OutputMode outputMode;

    public HashRowMapper(String hashField, OutputMode outputMode) {
        this.hashField = hashField;
        this.hashExtractor = VertexRowMapper.createFieldExtractor(hashField);
        this.outputMode = outputMode;
    }

    @Override
    public void flatMap(Record value, Collector<Row> out) {
        Vertex vertex = value.getVertex();
        Object hashVal = hashExtractor.extract(vertex);
        if (!(hashVal instanceof String)) {
            return;
        }

        String hashStr = ((String) hashVal).trim();
        if (hashStr.isEmpty() || !IocValidators.isValidHash(hashField, hashStr)) {
            return;
        }

        Row row = new Row(2);
        // 统一把 hash 顶点 key 规范化成 hash:<value> 再计算 VID。
        row.setField(0, VidHelper.vidFromHash(hashStr));
        row.setField(1, outputMode.resolveSecondField(vertex, hashStr));
        out.collect(row);
    }

    public enum OutputMode {
        HASH_VERTEX {
            @Override
            Object resolveSecondField(Vertex vertex, String hashStr) {
                return hashStr;
            }
        },
        HASH_EDGE {
            @Override
            Object resolveSecondField(Vertex vertex, String hashStr) {
                return vertex.vid();
            }
        };

        abstract Object resolveSecondField(Vertex vertex, String hashStr);
    }
}
