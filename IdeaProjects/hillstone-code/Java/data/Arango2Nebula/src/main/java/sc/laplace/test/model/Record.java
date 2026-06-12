package sc.laplace.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sc.laplace.test.constant.RecordType;
import sc.laplace.test.model.edge.Edge;
import sc.laplace.test.model.vertex.Vertex;

import java.io.Serializable;

/**
 * @author jxwu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Record implements Serializable {
    private RecordType type;
    private String collection;
    private Vertex vertex;
    private Edge edge;

    public static Record vertex(String collection, Vertex vertex) {
        return new Record(RecordType.VERTEX, collection, vertex, null);
    }

    public static Record edge(String collection, Edge edge) {
        return new Record(RecordType.EDGE, collection, null, edge);
    }
}
