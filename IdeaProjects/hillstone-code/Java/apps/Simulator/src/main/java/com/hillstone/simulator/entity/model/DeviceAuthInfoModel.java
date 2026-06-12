package com.hillstone.simulator.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author bohuachen
 * @date 2025/3/17 10:00
 * @description 认证内容
 */
@Data
public class DeviceAuthInfoModel {

    @JsonProperty("X-Auth-Token")
    private String xAuthToken;
    @JsonProperty("X-Auth-Role")
    private String xAuthRole;
    @JsonProperty("X-Auth-Username")
    private String xAuthUsername;
}
