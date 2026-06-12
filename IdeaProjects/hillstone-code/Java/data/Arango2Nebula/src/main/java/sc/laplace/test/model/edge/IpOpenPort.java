package sc.laplace.test.model.edge;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import sc.laplace.test.model.vertex.Ipv4;

/**
 * @author jxwu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IpOpenPort extends Ipv4 implements Edge {
    @JsonAlias({"port", "rank"})
    private Long rank;

    @Override
    public String fVid() {
        return vid();
    }

    @Override
    public String tVid() {
        return vid();
    }
}
