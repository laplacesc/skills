package com.hillstone.simulator.entity.qo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hillstone.simulator.entity.http.ai.BotChatS1MsgModel;
import lombok.Data;

import java.util.List;

/**
 * @author bohuachen
 * @date 2025/3/26 14:48
 * @description 运维助手提问第一步
 */
@Data
public class BotChatS1Qo {

    @JsonProperty("session_id")
    private String session_id;

    private List<BotChatS1MsgModel> messages;

    private boolean permission;

}
