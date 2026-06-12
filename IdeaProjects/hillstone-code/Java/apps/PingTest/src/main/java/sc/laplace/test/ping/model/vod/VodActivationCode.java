package sc.laplace.test.ping.model.vod;


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
public class VodActivationCode {
    /**
     * 设备SN
     */
    String deviceSn;
    /**
     * 违规外联检测激活码生成时间
     */
    Long generatedTime;
    /**
     * 主动探测配置 IP地址
     */
    String vodIp;
    /**
     * 被动探测配置 IP端口
     */
    Integer vodPort;
    /**
     * 被动探测配置 接口URL
     */
    String vodDetectionUrl;
    /**
     * 违规外联平台地址
     */
    String vodAddress;
}
