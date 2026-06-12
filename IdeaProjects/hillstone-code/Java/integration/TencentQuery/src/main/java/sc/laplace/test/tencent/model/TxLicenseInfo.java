package sc.laplace.test.tencent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author jxwu
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TxLicenseInfo {
    @JsonProperty("appkey")
    private String appkey;
    @JsonProperty("exprie_time")
    private String exprieTime;
    @JsonProperty("threat_package_type")
    private String threatPackageType;
    @JsonProperty("autorizar_product_type")
    private String autorizarProductType;
    @JsonProperty("is_sdk_pro")
    private Integer isSdkPro;

    public List<String> getThreatPackageType() {
        return Optional.ofNullable(threatPackageType)
                .map(s -> s.split(","))
                .map(s -> Arrays.stream(s).collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}
