package com.hillstone.simulator.entity.qo;

import lombok.Data;

/**
 * @author bohuachen
 * @date 2025/3/12 17:43
 * @description
 */
@Data
public class CreateDeviceQo {
    /**
     * 用户名称
     */
    private String username;
    /**
     * 设备序号
     */
    private String sn;
    /**
     * 防火墙名称
     */
    private String fwName;
    /**
     * 防火墙平台
     */
    private String platform;
    /**
     * 镜像版本
     */
    private String bootFile;
}
