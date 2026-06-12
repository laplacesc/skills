package sc.laplace.test.superset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.GuestTokenCreateRequest;
import sc.laplace.test.superset.model.GuestTokenCreateRequest.GuestResource;
import sc.laplace.test.superset.model.GuestTokenCreateRequest.GuestUser;
import sc.laplace.test.superset.model.GuestTokenResponse;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit tests for GuestTokenService using MockRestServiceServer.
 */
class GuestTokenServiceTest {

    private static final String BASE_URL = "http://superset:8088";
    private static final String GUEST_TOKEN_PATH = BASE_URL + "/api/v1/security/guest_token/";

    private MockRestServiceServer mockServer;
    private GuestTokenService guestTokenService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        SupersetClient client = new SupersetClient(restTemplate, BASE_URL);
        guestTokenService = new GuestTokenService(client);
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Test
    void testCreateDashboardToken() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7InVzZXJuYW1lIjoiZ3Vlc3QifX0.example";
        String responseBody = "{\"token\": \"" + token + "\"}";

        mockServer.expect(requestTo(GUEST_TOKEN_PATH))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        GuestTokenResponse response = guestTokenService.createDashboardToken(1);

        assertNotNull(response);
        assertEquals(token, response.getToken());
    }

    @Test
    void testCreateTokenWithUser() throws Exception {
        String token = "guest-token-abc-123";
        String responseBody = "{\"token\": \"" + token + "\"}";

        mockServer.expect(requestTo(GUEST_TOKEN_PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new GuestTokenCreateRequest(
                                new GuestUser("guest", "Guest", "User"),
                                Collections.singletonList(new GuestResource("dashboard", "1")),
                                Collections.emptyList()
                        )
                )))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        GuestTokenResponse response = guestTokenService.createToken(
                new GuestUser("guest", "Guest", "User"),
                Collections.singletonList(new GuestResource("dashboard", "1")),
                Collections.emptyList()
        );

        assertNotNull(response);
        assertEquals(token, response.getToken());
    }

    @Test
    void testCreateTokenWithMultipleResources() throws Exception {
        String token = "multi-resource-token";
        String responseBody = "{\"token\": \"" + token + "\"}";

        mockServer.expect(requestTo(GUEST_TOKEN_PATH))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        GuestTokenResponse response = guestTokenService.createToken(Arrays.asList(
                new GuestResource("dashboard", "1"),
                new GuestResource("chart", "5")
        ));

        assertNotNull(response);
        assertEquals(token, response.getToken());
    }
}
