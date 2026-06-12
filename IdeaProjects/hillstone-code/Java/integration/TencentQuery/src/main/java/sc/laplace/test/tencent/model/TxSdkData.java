package sc.laplace.test.tencent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

/**
 * @author jxwu
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TxSdkData {
    /**
     * 该字段为了记录当前查询的IOC
     */
    private String key;
    private String queryType;

    private Integer code;
    private String result;
    private Map<String, Object> meta;
    private Map<String, Object> info;
}
