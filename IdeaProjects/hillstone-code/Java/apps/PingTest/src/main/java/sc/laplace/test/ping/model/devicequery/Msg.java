package sc.laplace.test.ping.model.devicequery;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jxwu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Msg {
    /**
     * 设备SN
     */
    @JsonProperty(value = "device_sn", required = true)
    private String deviceSn;
    /**
     * 设备型号/平台
     */
    @JsonProperty(value = "device_platform", required = true)
    private String devicePlatform;
    /**
     * 请求时间
     */
    @JsonProperty(value = "request_time", required = true)
    private Long requestTime;

    @JsonProperty(value = "additional_properties")
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getResources() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void addResources(String k, Object v) {
        additionalProperties.put(k, v);
    }
}
