package sc.laplace.test.ping.model.vod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author jxwu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class VodDeviceDetectionData {
    /**
     * 设备SN
     */
    String deviceSn;
    /**
     * 请求时间
     */
    Long requestTime;
    /**
     * 终端内网IP
     */
    String localIp;
}
