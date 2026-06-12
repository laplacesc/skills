package com.hillstone.simulator.constant;

/**
 * @author: bohuachen
 * @date: 2023/6/20 9:50
 * @description: some desc
 */
public enum DeviceStatus {
    //"未启动"
    UNSTARTED,
    //"已连接",
    CONNECTED,
    //"schema已发送",
    SCHEMA_SENT,
    //("已注册", 3)
    REGISTERED,
    //("已发送心跳", 4)
    HEARTBEAT_SENT,
    //("断开连接", 5)
    DISCONNECTED,
    //("停止", 6)
    STOPED;
}
