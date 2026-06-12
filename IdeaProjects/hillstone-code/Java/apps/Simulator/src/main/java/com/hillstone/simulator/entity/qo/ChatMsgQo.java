package com.hillstone.simulator.entity.qo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @author bohuachen
 * @date 2025/3/12 17:43
 * @description
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@ToString
public class ChatMsgQo {

    /**
     * 本次提问
     */
    private String query;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 回复方式
     * blocking-阻塞式
     * streaming-流式式
     */
    private String responseMode;

    /**
     * 传入多组键值对
     */
    private Map<String, Object> inputs;

    /**
     * 设备信息
     */
    private Map<String, Object> deviceBasicInfos;

    /**
     * 用户验证
     */
    private Map<String, Object> deviceAuthInfos;

}
