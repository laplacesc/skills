package sc.laplace.test.aiioc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ti")
public class TiProperties {

    private String baseUrl;
    private String authToken;
    private String apiVersion;
    private String apiLanguage;
}
