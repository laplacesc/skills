package sc.laplace.test.ping.model.vod;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author jxwu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceIdentifier {
    /**
     * 设备SN
     */
    @NotEmpty
    String deviceSn;
    /**
     * 设备平台 SG-6000-S3500
     */
    @NotEmpty
    String devicePlatform;
    /**
     * 设备识别码生成时间
     */
    @NotEmpty
    Long generatedTime;
}
