package sc.laplace.test.mapper;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.types.Row;
import sc.laplace.test.model.Record;
import sc.laplace.test.model.edge.IpOpenPort;
import sc.laplace.test.model.edge.Relation;
import sc.laplace.test.util.JsonUtil;

import java.util.Map;

public class EdgeRowMapper implements MapFunction<Record, Row> {
    /**
     * 普通边的一对一映射。
     */
    @Override
    public Row map(Record value) {
        // Nebula edge sink 约定 Row = [srcVid, dstVid, rank]。
        Row row = new Row(3);
        row.setField(0, value.getEdge().fVid());
        row.setField(1, value.getEdge().tVid());
        row.setField(2, resolveRank(value));
        return row;
    }

    private Long resolveRank(Record value) {
        if (value.getEdge() instanceof Relation) {
            Long rank = ((Relation) value.getEdge()).getRank();
            return rank == null ? 0L : rank;
        }
        if (value.getEdge() instanceof IpOpenPort) {
            Long rank = ((IpOpenPort) value.getEdge()).getRank();
            return rank == null ? 0L : rank;
        }

        // 最后兜底从 JSON 属性取 rank，兼容暂时未建强类型模型的 edge。
        Map<String, Object> props = JsonUtil.toMap(value.getEdge());
        Object rankObj = props.get("rank");
        if (rankObj instanceof Number) {
            return ((Number) rankObj).longValue();
        }
        if (rankObj != null) {
            try {
                return Long.parseLong(rankObj.toString());
            } catch (NumberFormatException ignored) {
                return 0L;
            }
        }
        return 0L;
    }
}
