package sc.laplace.test.model.vertex;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import sc.laplace.test.util.VidHelper;

import java.io.Serializable;

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
public class Url extends Ioc implements Vertex, Serializable {
    /**
     * URL路径
     */
    @JsonProperty("url")
    private String url;

    @Override
    public String vid() {
        return VidHelper.vidFromUrl(url);
    }
}
