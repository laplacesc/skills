package sc.laplace.test.superset.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Apache Superset connection.
 */
@ConfigurationProperties(prefix = "superset")
public class SupersetProperties {

    /** Base URL of the Superset instance, e.g. http://localhost:8088 */
    private String baseUrl = "http://localhost:8088";

    /** Username for password-based authentication */
    private String username;

    /** Password for password-based authentication */
    private String password;

    /** Pre-obtained JWT access token (alternative to username/password) */
    private String accessToken;

    /** Connection timeout in milliseconds */
    private int connectTimeout = 10000;

    /** Read timeout in milliseconds */
    private int readTimeout = 30000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
