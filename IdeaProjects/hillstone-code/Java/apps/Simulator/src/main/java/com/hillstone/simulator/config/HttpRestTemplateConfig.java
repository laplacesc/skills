package com.hillstone.simulator.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @author: bohuachen
 * @date: 2023/6/19 17:40
 * @description: some desc
 */
@Slf4j
@ConditionalOnProperty(name = "device.connect.info.http_link_mode",havingValue = "http")
@Configuration
public class HttpRestTemplateConfig {

    @Value("${rest.connect.timeout}")
    private int connectionTimeout;
    @Value("${rest.request.timeout}")
    private int requestTimeout;
    @Value("${rest.socket.timeout}")
    private int socketTimeout;


    @Bean
    public RestTemplate restTemplate() {
        log.info("init RestTemplate...");
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate();
        factory.setConnectTimeout(connectionTimeout);
        factory.setConnectionRequestTimeout(requestTimeout);
        factory.setReadTimeout(socketTimeout);
        // 注释的部分用来设置restTemplate的String的转换编码，默认是ISO-8859-1
        restTemplate.setRequestFactory(factory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

}
