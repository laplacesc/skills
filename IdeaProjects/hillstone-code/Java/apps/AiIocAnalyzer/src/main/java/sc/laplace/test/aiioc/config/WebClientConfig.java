package sc.laplace.test.aiioc.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WebClientConfig {

    @Bean
    public RestTemplate aiRestTemplate(RestTemplateBuilder builder, AiProperties aiProperties) {
        return builder
                .rootUri(aiProperties.getBaseUrl())
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService aiStreamExecutor() {
        return Executors.newFixedThreadPool(8);
    }
}
