package com.hillstone.simulator.service;

import com.hillstone.simulator.entity.MessageObject;

/**
 * 设备注册调用过程接口 实现分为 单台设备和多台设备
 *
 * @author rtzhang
 * @date 2023/10/31 11:12
 * @description
 */
public interface DeviceRegisterInterface {
    /**
     * 设备注册流程
     */
    void deviceRegister();

    /**
     * 平台消息处理(配置相关转到)
     *
     * @param msg
     */
    void messageProcess(String msg);

    /**
     * hello 流程
     */
    void helloProcess();

    /**
     * requestCapabilities 流程
     */
    void requestCapabilitiesProcess();

    /**
     * requestSchemas 流程
     *
     * @param mo
     */
    void requestSchemasProcess(MessageObject mo);

    /**
     * reqDeviceBasicInfo 流程
     *
     * @param mo
     */
    void reqDeviceBasicInfoProcess(MessageObject mo);

    /**
     * 注册后流程
     */
    void registeredProcess();
}
