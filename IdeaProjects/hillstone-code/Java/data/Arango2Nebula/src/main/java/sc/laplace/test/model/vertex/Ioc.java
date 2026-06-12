package sc.laplace.test.model.vertex;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author jxwu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Ioc implements Vertex, Serializable {
    /**
     * 创建时间（Unix 时间戳）
     */
    @JsonProperty("create_time")
    @JsonSetter(nulls = Nulls.SKIP)
    protected Long createTime = 0L;
    /**
     * 上次更新时间（Unix 时间戳）
     */
    @JsonProperty("update_time")
    @JsonSetter(nulls = Nulls.SKIP)
    protected Long updateTime = 0L;
    /**
     * 威胁类型（标识情报的具体类别）：ip/domain/file/url
     */
    @JsonProperty("type")
    protected String type;
    /**
     * 鉴定结果：0-Unknown, 1-White, 2-Suspicious, 3-Black
     */
    @JsonProperty("result")
    @JsonSetter(nulls = Nulls.SKIP)
    protected Integer result = 0;
    /**
     * 置信度
     */
    @JsonProperty("credibility")
    protected Integer credibility;
    /**
     * 生命周期
     */
    @JsonProperty("lifecycle_status")
    protected Integer lifecycleStatus;
    /**
     * 出入站
     */
    @JsonProperty("flow_direction")
    @JsonSetter(nulls = Nulls.SKIP)
    protected Integer flowDirection = 0;
    /**
     * 首次发现时间
     */
    @JsonProperty("first_seen")
    protected Long firstSeen;
    /**
     * 最后一次发现时间
     */
    @JsonProperty("last_seen")
    protected Long lastSeen;
    /**
     * IOC的恶意程度，0-5，未知/无风险/低危/中危/高危/严重
     */
    @JsonProperty("malicious_level")
    @JsonSetter(nulls = Nulls.SKIP)
    protected Integer maliciousLevel = 0;
}
