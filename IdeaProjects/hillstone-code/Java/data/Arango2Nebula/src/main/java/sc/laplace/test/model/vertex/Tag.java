package sc.laplace.test.model.vertex;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sc.laplace.test.util.VidHelper;

import java.io.Serializable;

/**
 * @author jxwu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tag implements Vertex, Serializable {
    /**
     * id
     */
    @JsonProperty("id")
    private Long id;
    /**
     * 一级分类
     */
    @JsonProperty("type")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer type = 0;
    /**
     * 二级分类
     */
    @JsonProperty("subtype")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer subtype = 0;
    /**
     * 名称(中文)
     */
    @JsonProperty("name_cn")
    private String nameCn;
    /**
     * 名称(英文)
     */
    @JsonProperty("name_en")
    private String nameEn;
    /**
     * 描述(中文)
     */
    @JsonProperty("description_cn")
    private String descriptionCn;
    /**
     * 描述(英文)
     */
    @JsonProperty("description_en")
    private String descriptionEn;
    /**
     * 可视性（0:public , 1:private）
     */
    @JsonProperty("visibility")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer visibility = 0;
    /**
     * 严重级别
     */
    @JsonProperty("severity")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer severity = 0;

    @Override
    public String vid() {
        return VidHelper.vidFromTagId(id);
    }
}
