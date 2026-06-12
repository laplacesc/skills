package com.hillstone.simulator.client;

import com.hillstone.simulator.service.single.SingleDeviceRegisterService;
import com.hillstone.simulator.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author: bohuachen
 * @date: 2023/6/16 8:30
 * @description: some desc
 */
@Slf4j
public class DeviceWebSocketClient extends WebSocketClient {


    public DeviceWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("websocket open !!!!!");
    }

    @Override
    public void onMessage(String msg) {
        log.info("get websocket msg : {}", msg);
        SpringUtils.getBean(SingleDeviceRegisterService.class).messageProcess(msg);
    }

    @Override
    public void onClose(int closeCode, String reason, boolean b) {
        log.info("websocket close : closeCode : {}, reason : {}", closeCode, reason);
    }

    @Override
    public void onError(Exception e) {
        log.error(e.getMessage(), e);
    }
}
