package sc.laplace.test.superset.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import sc.laplace.test.superset.client.SupersetClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;

/**
 * Spring configuration that exposes a pre-configured RestTemplate bean
 * with the Superset auth interceptor applied.
 * <p>
 * The RestTemplate uses {@link Proxy#NO_PROXY} to bypass any system-wide
 * HTTP proxy (e.g. Clash, Charles) when connecting to Superset.
 */
@Configuration
@EnableConfigurationProperties(SupersetProperties.class)
public class SupersetConfig {

    @Bean
    public RestTemplate supersetRestTemplate(SupersetProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeout());
        factory.setReadTimeout(properties.getReadTimeout());
        // Bypass any system-wide HTTP proxy (Clash, Charles, etc.)
        factory.setProxy(Proxy.NO_PROXY);

        RestTemplate restTemplate = new RestTemplate(factory);
        SupersetAuthInterceptor interceptor = new SupersetAuthInterceptor(properties, restTemplate);
        restTemplate.setInterceptors(Collections.singletonList(interceptor));
        return restTemplate;
    }

    @Bean
    public SupersetClient supersetClient(RestTemplate supersetRestTemplate,
                                         SupersetProperties properties) {
        return new SupersetClient(supersetRestTemplate, properties.getBaseUrl());
    }
}
