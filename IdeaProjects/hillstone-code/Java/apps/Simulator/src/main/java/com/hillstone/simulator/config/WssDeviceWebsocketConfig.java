package com.hillstone.simulator.config;

import com.hillstone.simulator.client.DeviceWebSocketClient;
import com.hillstone.simulator.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author: bohuachen
 * @date: 2023/6/16 8:38
 * @description: some desc
 */
@Slf4j
@ConditionalOnProperty(name = "device.connect.info.ws_link_mode", havingValue = "wss")
@Configuration
public class WssDeviceWebsocketConfig {


    @Autowired
    private DeviceInfoConfig deviceInfoConfig;


    @ConditionalOnExpression("'${process.model}'.equals('1') && '${multi.enable}'.equals('false')")
    @Bean("deviceWebSocketClient")
    public WebSocketClient deviceWebSocketClient() throws URISyntaxException {
        DeviceWebSocketClient webSocketClient = new DeviceWebSocketClient(new URI(deviceInfoConfig.getWsUrl()));
        SSLContext sslContext = HttpUtils.getSSLContent();
        SSLSocketFactory factory = sslContext.getSocketFactory();
        webSocketClient.setSocketFactory(factory);
        webSocketClient.connect();
        return webSocketClient;
    }


}
