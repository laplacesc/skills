package com.hillstone.simulator.config;

import com.hillstone.simulator.client.DeviceWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author: bohuachen
 * @date: 2023/6/16 8:38
 * @description: some desc
 */
@Slf4j
@ConditionalOnProperty(name = "device.connect.info.ws_link_mode", havingValue = "ws")
@Configuration
public class WsDeviceWebsocketConfig {


    @Autowired
    private DeviceInfoConfig deviceInfoConfig;


    @ConditionalOnExpression("'${process.model}'.equals('1') && '${multi.enable}'.equals('false')")
    @Bean("deviceWebSocketClient")
    public WebSocketClient deviceWebSocketClient() throws URISyntaxException {
        DeviceWebSocketClient webSocketClient = new DeviceWebSocketClient(new URI(deviceInfoConfig.getWsUrl()));
        webSocketClient.connect();
        return webSocketClient;
    }


}
