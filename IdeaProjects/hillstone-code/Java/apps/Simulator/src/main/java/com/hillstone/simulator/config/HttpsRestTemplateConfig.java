package com.hillstone.simulator.config;

import com.hillstone.simulator.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

/**
 * @author: bohuachen
 * @date: 2023/6/19 17:40
 * @description: some desc
 */
@Slf4j
@ConditionalOnProperty(name = "device.connect.info.http_link_mode", havingValue = "https")
@Configuration
public class HttpsRestTemplateConfig {

    @Value("${rest.connect.timeout}")
    private int connectionTimeout;
    @Value("${rest.request.timeout}")
    private int requestTimeout;
    @Value("${rest.socket.timeout}")
    private int socketTimeout;


    @Bean
    public RestTemplate restTemplate() {
        log.info("init RestTemplate...");
        SSLContext sslContext = HttpUtils.getSSLContent();
        SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient httpClient = HttpClients.custom().setMaxConnTotal(50).setMaxConnPerRoute(5).setConnectionTimeToLive(3, TimeUnit.MINUTES).setSSLSocketFactory(connectionSocketFactory).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(connectionTimeout);
        factory.setConnectionRequestTimeout(requestTimeout);
        factory.setReadTimeout(socketTimeout);
        return new RestTemplate(factory);
    }

}
