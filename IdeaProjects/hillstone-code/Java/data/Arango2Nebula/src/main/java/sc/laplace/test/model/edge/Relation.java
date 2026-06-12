package sc.laplace.test.model.edge;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import sc.laplace.test.util.DigestHelper;

/**
 * @author jxwu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Relation implements Edge {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String from;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String to;
    @JsonAlias({"update_time", "rank"})
    protected Long rank;

    @Override
    public String fVid() {
        return DigestHelper.sha256Hex(from);
    }

    @Override
    public String tVid() {
        return DigestHelper.sha256Hex(to);
    }
}
