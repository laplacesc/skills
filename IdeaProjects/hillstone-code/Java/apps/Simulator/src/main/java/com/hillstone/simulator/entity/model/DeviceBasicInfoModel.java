package com.hillstone.simulator.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author bohuachen
 * @date 2025/3/17 10:00
 * @description
 */
@Data
public class DeviceBasicInfoModel {

    /**
     * 设备产品线
     */
    @JsonProperty("product-family")
    private String productFamily;
    /**
     * 设备型号
     */
    @JsonProperty("product-name")
    private String productName;
    /**
     * 软件版本
     */
    @JsonProperty("software-version")
    private String softwareVersion;
}
