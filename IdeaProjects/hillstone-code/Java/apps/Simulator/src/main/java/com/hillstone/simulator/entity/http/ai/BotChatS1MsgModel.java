package com.hillstone.simulator.entity.http.ai;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author bohuachen
 * @date 2025/3/26 14:48
 * @description 运维助手提问第一步 消息体
 */
@Data
public class BotChatS1MsgModel {

    private String role;
    private List<Map<String, Object>> content;

}
