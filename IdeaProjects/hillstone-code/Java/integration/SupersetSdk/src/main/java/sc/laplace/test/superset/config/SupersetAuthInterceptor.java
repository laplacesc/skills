package sc.laplace.test.superset.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * Authentication interceptor for Apache Superset REST API.
 * <p>
 * Supports two authentication modes:
 * <ol>
 *   <li><b>Token-based (JWT)</b> — used by Superset 3.x/4.x. Sends
 *       {@code Authorization: Bearer <token>} on every request.</li>
 *   <li><b>Session + CSRF</b> — used by older Superset versions.
 *       Performs a login request, extracts the session cookie, and
 *       includes it along with the {@code X-CSRFToken} header.</li>
 * </ol>
 * <p>
 * If an {@code accessToken} is provided in properties, token mode is used.
 * Otherwise, the interceptor performs a password-based login on the first
 * request, extracts the session cookie and the access token from the login
 * response, and applies them to subsequent requests.
 */
public class SupersetAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SupersetAuthInterceptor.class);

    private static final String LOGIN_PATH = "/api/v1/security/login";
    private static final String CSRF_TOKEN_PATH = "/api/v1/security/csrf_token/";
    private static final String COOKIE_SESSION = "session";

    private final SupersetProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private volatile String accessToken;
    private volatile String sessionCookie;
    private volatile String csrfToken;
    private volatile boolean authenticated = false;
    private final Object authLock = new Object();

    public SupersetAuthInterceptor(SupersetProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();

        if (properties.getAccessToken() != null && !properties.getAccessToken().isEmpty()) {
            this.accessToken = properties.getAccessToken();
            this.authenticated = true;
            log.debug("Superset interceptor initialized with pre-configured access token");
        }
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String path = request.getURI().getPath();

        // Bypass interceptor for login and CSRF token requests to avoid infinite recursion
        if (path.contains(LOGIN_PATH) || path.contains(CSRF_TOKEN_PATH)) {
            return execution.execute(request, body);
        }

        if (!authenticated) {
            authenticate();
        }

        // Apply auth headers
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        if (sessionCookie != null) {
            request.getHeaders().add(HttpHeaders.COOKIE, sessionCookie);
        }
        if (csrfToken != null) {
            request.getHeaders().add("X-CSRFToken", csrfToken);
        }
        request.getHeaders().add(HttpHeaders.REFERER, properties.getBaseUrl() + "/");

        ClientHttpResponse response = execution.execute(request, body);

        // If we get 401, re-authenticate and retry once
        if (response.getRawStatusCode() == 401 && authenticated) {
            log.info("Received 401 from Superset, re-authenticating...");
            synchronized (authLock) {
                authenticated = false;
                authenticate();
            }
            // Re-apply auth headers
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            if (sessionCookie != null) {
                request.getHeaders().set(HttpHeaders.COOKIE, sessionCookie);
            }
            if (csrfToken != null) {
                request.getHeaders().set("X-CSRFToken", csrfToken);
            }
            response = execution.execute(request, body);
        }

        return response;
    }

    /**
     * Performs authentication with Superset.
     * Tries password-based login first; if an access token is already set,
     * attempts to fetch CSRF token directly.
     */
    private void authenticate() {
        synchronized (authLock) {
            if (authenticated) return;

            try {
                if (accessToken == null) {
                    performLogin();
                }
                // Try to acquire CSRF token (may fail on newer Superset versions)
                try {
                    fetchCsrfToken();
                } catch (Exception e) {
                    log.debug("CSRF token acquisition not supported on this Superset version (ignored): {}", e.getMessage());
                }
                authenticated = true;
                log.info("Successfully authenticated with Superset at {}", properties.getBaseUrl());
            } catch (Exception e) {
                log.error("Failed to authenticate with Superset at {}: {}", properties.getBaseUrl(), e.getMessage());
                throw new RuntimeException("Superset authentication failed", e);
            }
        }
    }

    private void performLogin() {
        try {
            // Build login request body
            String loginPayload = objectMapper.writeValueAsString(
                    new LoginRequestBody(properties.getUsername(), properties.getPassword()));

            org.springframework.http.HttpEntity<String> loginEntity =
                    new org.springframework.http.HttpEntity<>(loginPayload, createJsonHeaders());

            // Use a plain RestTemplate (without interceptors) for the login call,
            // with Proxy.NO_PROXY to bypass any system-wide HTTP proxy
            SimpleClientHttpRequestFactory noProxyFactory = new SimpleClientHttpRequestFactory();
            noProxyFactory.setProxy(java.net.Proxy.NO_PROXY);
            RestTemplate plainRestTemplate = new RestTemplate(noProxyFactory);

            org.springframework.http.ResponseEntity<JsonNode> loginResponse =
                    plainRestTemplate.postForEntity(
                            properties.getBaseUrl() + LOGIN_PATH,
                            loginEntity,
                            JsonNode.class);

            JsonNode loginBody = loginResponse.getBody();
            if (loginBody != null && loginBody.has("access_token")) {
                accessToken = loginBody.get("access_token").asText();
                log.debug("Obtained JWT access token from login");
            }

            // Extract session cookie from Set-Cookie headers
            List<String> setCookieHeaders = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookieHeaders != null) {
                sessionCookie = setCookieHeaders.stream()
                        .filter(c -> c.startsWith(COOKIE_SESSION))
                        .findFirst()
                        .orElse(null);
                if (sessionCookie == null) {
                    // Just take all cookies as they came
                    sessionCookie = String.join("; ", setCookieHeaders);
                }
                log.debug("Extracted session cookie");
            }

        } catch (Exception e) {
            log.error("Login request to Superset failed (url={})", properties.getBaseUrl() + LOGIN_PATH, e);
            if (e instanceof org.springframework.web.client.HttpClientErrorException) {
                org.springframework.web.client.HttpClientErrorException he = (org.springframework.web.client.HttpClientErrorException) e;
                log.error("HTTP error: status={}, responseBody={}", he.getStatusCode(), he.getResponseBodyAsString());
            } else if (e instanceof org.springframework.web.client.ResourceAccessException) {
                log.error("Connection error: {}", e.getMessage());
            }
            throw new RuntimeException("Login request to Superset failed", e);
        }
    }

    private void fetchCsrfToken() {
        try {
            // CSRF token endpoint is GET, not POST
            // Use a plain RestTemplate (without interceptors) for the CSRF token call,
            // with Proxy.NO_PROXY to bypass any system-wide HTTP proxy
            SimpleClientHttpRequestFactory noProxyFactory = new SimpleClientHttpRequestFactory();
            noProxyFactory.setProxy(java.net.Proxy.NO_PROXY);
            RestTemplate plainRestTemplate = new RestTemplate(noProxyFactory);

            org.springframework.http.ResponseEntity<JsonNode> csrfResponse =
                    plainRestTemplate.exchange(
                            properties.getBaseUrl() + CSRF_TOKEN_PATH,
                            org.springframework.http.HttpMethod.GET,
                            new org.springframework.http.HttpEntity<>(createJsonHeadersWithAuth()),
                            JsonNode.class);

            JsonNode csrfBody = csrfResponse.getBody();
            if (csrfBody != null && csrfBody.has("result")) {
                csrfToken = csrfBody.get("result").asText();
                log.debug("Obtained CSRF token");
            }
        } catch (Exception e) {
            log.debug("Superset does not support CSRF token endpoint: {}", e.getMessage());
        }
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders createJsonHeadersWithAuth() {
        HttpHeaders headers = createJsonHeaders();
        if (accessToken != null) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        }
        if (sessionCookie != null) {
            headers.set(HttpHeaders.COOKIE, sessionCookie);
        }
        return headers;
    }

    /**
     * Simple POJO for the login request body.
     */
    private static class LoginRequestBody {
        public final String username;
        public final String password;
        public final String provider = "db";
        public final boolean refresh = true;

        LoginRequestBody(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
