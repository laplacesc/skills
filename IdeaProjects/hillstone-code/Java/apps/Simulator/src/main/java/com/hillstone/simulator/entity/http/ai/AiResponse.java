package com.hillstone.simulator.entity.http.ai;

import lombok.Data;

/**
 * @author bohuachen
 * @date 2025/3/19 13:49
 * @description
 */
@Data
public class AiResponse {

    private int code;

    private String message;

    private AiResult result;
}
