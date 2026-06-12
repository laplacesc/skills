package sc.laplace.test.superset.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import sc.laplace.test.superset.exception.SupersetApiException;
import sc.laplace.test.superset.model.GuestTokenCreateRequest;
import sc.laplace.test.superset.model.GuestTokenResponse;
import sc.laplace.test.superset.model.PaginatedResponse;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Central HTTP client for the Apache Superset REST API.
 * <p>
 * All public methods use {@code /api/v1/} as the base path automatically.
 */
public class SupersetClient {

    private static final Logger log = LoggerFactory.getLogger(SupersetClient.class);

    private static final String API_BASE = "/api/v1/";

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public SupersetClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Propagate the configured ObjectMapper to the RestTemplate's Jackson converters
        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).setObjectMapper(this.objectMapper);
            }
        }
    }

    // ---------------------------------------------------------------
    // Generic HTTP methods
    // ---------------------------------------------------------------

    /**
     * Perform a GET request and return the response body of the specified type.
     */
    public <T> T get(String path, Class<T> responseType) {
        return execute(() -> {
            ResponseEntity<T> response = restTemplate.getForEntity(fullUrl(path), responseType);
            return response.getBody();
        }, "GET", path);
    }

    /**
     * Perform a GET request that returns a list of items wrapped in a JSON array.
     */
    public <T> List<T> getList(String path, Class<T> elementType) {
        return execute(() -> {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(fullUrl(path), JsonNode.class);
            JsonNode body = response.getBody();
            if (body == null) {
                return Collections.emptyList();
            }

            // Try to unwrap the "result" array from paginated response
            JsonNode result = body.has("result") ? body.get("result") : body;
            if (result.isArray()) {
                JavaType type = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, elementType);
                return objectMapper.convertValue(result, type);
            }
            return Collections.emptyList();
        }, "GET", path);
    }

    /**
     * Perform a GET request and return a paginated response.
     */
    public <T> PaginatedResponse<T> getPaginated(String path, Class<T> elementType) {
        return execute(() -> {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(fullUrl(path), JsonNode.class);
            JsonNode body = response.getBody();
            if (body == null) {
                return PaginatedResponse.empty();
            }

            PaginatedResponse<T> pr = new PaginatedResponse<>();
            if (body.has("count")) {
                pr.setCount(body.get("count").asLong());
            }
            if (body.has("ids")) {
                JavaType idsType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, Integer.class);
                pr.setIds(objectMapper.convertValue(body.get("ids"), idsType));
            }
            if (body.has("result") && body.get("result").isArray()) {
                JavaType resultType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, elementType);
                pr.setResult(objectMapper.convertValue(body.get("result"), resultType));
            }
            return pr;
        }, "GET", path);
    }

    /**
     * Perform a POST request.
     */
    public <T, R> R post(String path, T requestBody, Class<R> responseType) {
        return execute(() -> {
            HttpEntity<T> entity = new HttpEntity<>(requestBody, createJsonHeaders());
            ResponseEntity<R> response = restTemplate.exchange(
                    fullUrl(path), HttpMethod.POST, entity, responseType);
            return response.getBody();
        }, "POST", path);
    }

    /**
     * Perform a POST request that returns a JsonNode (for flexible responses).
     */
    public <T> JsonNode postForJson(String path, T requestBody) {
        return execute(() -> {
            HttpEntity<T> entity = new HttpEntity<>(requestBody, createJsonHeaders());
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    fullUrl(path), HttpMethod.POST, entity, JsonNode.class);
            return response.getBody();
        }, "POST", path);
    }

    /**
     * Create a guest token for embedding Superset resources (dashboards, charts)
     * into external applications.
     * <p>
     * Calls {@code POST /api/v1/security/guest_token/}.
     *
     * @param request the guest token request detailing user, resources, and RLS rules
     * @return the guest token response containing the JWT token
     */
    public GuestTokenResponse createGuestToken(GuestTokenCreateRequest request) {
        return post("api/v1/security/guest_token/", request, GuestTokenResponse.class);
    }

    /**
     * Perform a PUT request.
     */
    public <T, R> R put(String path, T requestBody, Class<R> responseType) {
        return execute(() -> {
            HttpEntity<T> entity = new HttpEntity<>(requestBody, createJsonHeaders());
            ResponseEntity<R> response = restTemplate.exchange(
                    fullUrl(path), HttpMethod.PUT, entity, responseType);
            return response.getBody();
        }, "PUT", path);
    }

    /**
     * Perform a DELETE request.
     */
    public void delete(String path) {
        execute(() -> {
            restTemplate.delete(fullUrl(path));
            return null;
        }, "DELETE", path);
    }

    /**
     * Download raw bytes (e.g. for dashboard/chart export).
     */
    public byte[] download(String path) {
        return execute(() -> {
            HttpEntity<Void> entity = new HttpEntity<>(createJsonHeaders());
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    fullUrl(path), HttpMethod.GET, entity, byte[].class);
            return response.getBody();
        }, "GET", path);
    }

    /**
     * Perform a GET request and return raw JSON as a JsonNode.
     */
    public JsonNode getAsJsonNode(String path) {
        return execute(() -> {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(fullUrl(path), JsonNode.class);
            return response.getBody();
        }, "GET", path);
    }

    // ---------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------

    private String fullUrl(String path) {
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
        String base = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        return base + normalizedPath;
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @FunctionalInterface
    private interface ApiCall<T> {
        T call();
    }

    private <T> T execute(ApiCall<T> call, String method, String path) {
        try {
            return call.call();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String errorMessage = extractErrorMessage(e);
            log.error("Superset API {} {} failed: {} (status {})",
                    method, path, errorMessage, e.getRawStatusCode());
            throw new SupersetApiException(
                    e.getRawStatusCode(), path, errorMessage, e);
        } catch (Exception e) {
            log.error("Superset API {} {} failed after HTTP response: {}", method, path, e.getMessage());
            throw new SupersetApiException(0, path, e.getMessage(), e);
        }
    }

    private String extractErrorMessage(HttpStatusCodeException e) {
        try {
            String body = e.getResponseBodyAsString();
            log.error("Raw response body: {}", body);
            JsonNode node = objectMapper.readTree(body);
            if (node.has("message")) {
                return node.get("message").asText();
            }
            if (node.has("error")) {
                return node.get("error").asText();
            }
            return body;
        } catch (Exception ignored) {
            log.error("Raw response body (parse failed): {}", e.getResponseBodyAsString());
            return e.getMessage();
        }
    }
}
