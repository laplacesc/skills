package sc.laplace.test.tencent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jxwu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TencentQueryParam {
    @JsonProperty("c_action")
    private String cAction;
    @JsonProperty("c_appid")
    private String cAppId;
    @JsonProperty("c_nonce")
    private String cNonce;
    @JsonProperty("c_timestamp")
    private long cTimestamp;
    @JsonProperty("c_version")
    private String cVersion;
    @JsonProperty("c_signature")
    private String cSignature;
    @JsonProperty("key")
    private String key;
    @JsonProperty("option")
    private Integer option;
    @JsonProperty("type")
    private String type;
}
