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
public class Ipv4 extends Ioc implements Vertex, Serializable {
    /**
     * Ip地址
     */
    @JsonProperty("ip_address")
    protected String ipAddress;
    /**
     * 证书
     */
    @JsonProperty("certificate")
    protected String certificate;

    @Override
    public String vid() {
        return VidHelper.vidFromIpv4(ipAddress);
    }
}
