package com.hillstone.simulator.service.config;

import com.hillstone.simulator.entity.MessageObject;
import com.hillstone.simulator.service.multi.MultiDeviceConfigModel;

/**
 * @author: bohuachen
 * @date: 2023/6/25 9:38
 * @description: 配置流程interface 主要分为3类 1直接打印结果，不需要处理 2需要通过控制通道返回数据， 3需要通过数据通道返回数据
 */
public interface ConfigProcessInterface {

    /**
     * 配置处理流程
     *
     * @param mo
     */
    void runConfigProcess(MessageObject mo);

    /**
     * 多设备配置处理流程
     *
     * @param mo
     * @param md
     */
    void runMultiConfigProcess(MessageObject mo, MultiDeviceConfigModel md);

}
