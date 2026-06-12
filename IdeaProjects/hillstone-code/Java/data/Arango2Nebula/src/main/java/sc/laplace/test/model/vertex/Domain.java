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
public class Domain extends Ioc implements Vertex, Serializable {
    /**
     * 域名
     */
    @JsonProperty("domain_name")
    private String domainName;
    /**
     * 详细类型
     */
    @JsonProperty("detail_type")
    private String detailType;
    /**
     * 可注册域名
     */
    @JsonProperty("top_domain_name")
    private String topDomainName;
    /**
     * HTTPS证书
     */
    @JsonProperty("certificate")
    private String certificate;

    @Override
    public String vid() {
        return VidHelper.vidFromDomain(domainName);
    }
}
