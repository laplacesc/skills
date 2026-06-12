package sc.laplace.test.superset.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.SqlExecuteRequest;
import sc.laplace.test.superset.model.SqlExecuteResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit tests for SqlLabService using MockRestServiceServer.
 */
class SqlLabServiceTest {

    private static final String BASE_URL = "http://superset:8088";
    private static final String EXECUTE_PATH = BASE_URL + "/api/v1/sqllab/execute/";

    private MockRestServiceServer mockServer;
    private SqlLabService sqlLabService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        SupersetClient client = new SupersetClient(restTemplate, BASE_URL);
        sqlLabService = new SqlLabService(client);
    }

    @Test
    void testExecuteQuery() {
        String responseBody = "{\n" +
                "  \"query_id\": \"q-abc123\",\n" +
                "  \"status\": \"pending\"\n" +
                "}";

        mockServer.expect(requestTo(EXECUTE_PATH))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        SqlExecuteRequest request = new SqlExecuteRequest(1, "SELECT * FROM test");
        SqlExecuteResponse response = sqlLabService.execute(request);

        assertNotNull(response);
        assertEquals("q-abc123", response.getQueryId());
        assertEquals("pending", response.getStatus());
        assertTrue(response.isPending());
    }
}
