package sc.laplace.test.tencent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sc.laplace.test.tencent.constant.SdkPackageMod;

/**
 * @author jxwu
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SdkPackageDownloadParam {
    @JsonProperty("appkey")
    private String appkey;
    @JsonProperty("package_mod")
    private SdkPackageMod packageMod;
    @JsonProperty("package_type_string")
    private String packageTypeString;
    @JsonProperty("package_type")
    private String packageType;
    @JsonProperty("req_type")
    private String reqType;
    @JsonProperty("is_rocksdb")
    private Integer isRocksdb;
}
