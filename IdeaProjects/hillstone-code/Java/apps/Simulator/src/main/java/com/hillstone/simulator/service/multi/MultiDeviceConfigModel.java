package com.hillstone.simulator.service.multi;

import com.hillstone.simulator.client.MultiDeviceWebSocketClient;

/**
 * @author rtzhang
 * @date 2023/11/3 11:19
 * @description 每台设备的独立配置
 */
public class MultiDeviceConfigModel {
    private String sn;
    private String userName;
    private MultiDeviceWebSocketClient multiDeviceWebSocketClient;

    public MultiDeviceConfigModel(String sn, String userName, MultiDeviceWebSocketClient multiDeviceWebSocketClient) {
        this.sn = sn;
        this.userName = userName;
        this.multiDeviceWebSocketClient = multiDeviceWebSocketClient;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public MultiDeviceWebSocketClient getMultiDeviceWebSocketClient() {
        return multiDeviceWebSocketClient;
    }

    public void setMultiDeviceWebSocketClient(MultiDeviceWebSocketClient multiDeviceWebSocketClient) {
        this.multiDeviceWebSocketClient = multiDeviceWebSocketClient;
    }
}
