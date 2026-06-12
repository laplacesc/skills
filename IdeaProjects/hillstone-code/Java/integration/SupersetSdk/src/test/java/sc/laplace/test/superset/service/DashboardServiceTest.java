package sc.laplace.test.superset.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.DashboardCreateRequest;
import sc.laplace.test.superset.model.DashboardDetail;
import sc.laplace.test.superset.model.DashboardInfo;
import sc.laplace.test.superset.model.PaginatedResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit tests for DashboardService using MockRestServiceServer.
 */
class DashboardServiceTest {

    private static final String BASE_URL = "http://superset:8088";
    private static final String DASHBOARD_PATH = BASE_URL + "/api/v1/dashboard/";

    private MockRestServiceServer mockServer;
    private DashboardService dashboardService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        SupersetClient client = new SupersetClient(restTemplate, BASE_URL);
        dashboardService = new DashboardService(client);
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    void testListDashboards() throws Exception {
        String responseBody = "{\n" +
                "  \"count\": 2,\n" +
                "  \"ids\": [1, 2],\n" +
                "  \"result\": [\n" +
                "    {\"id\": 1, \"dashboard_title\": \"Dashboard One\"},\n" +
                "    {\"id\": 2, \"dashboard_title\": \"Dashboard Two\"}\n" +
                "  ]\n" +
                "}";

        mockServer.expect(requestTo(DASHBOARD_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        PaginatedResponse<DashboardInfo> response = dashboardService.list();

        assertEquals(2, response.getCount());
        assertNotNull(response.getResult());
        assertEquals(2, response.getResult().size());
        assertEquals(1, response.getResult().get(0).getId().intValue());
    }

    @Test
    void testCreateDashboard() throws Exception {
        DashboardCreateRequest request = new DashboardCreateRequest("New Dashboard");

        String responseBody = "{\n" +
                "  \"id\": 10,\n" +
                "  \"dashboard_title\": \"New Dashboard\"\n" +
                "}";

        mockServer.expect(requestTo(DASHBOARD_PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        DashboardDetail result = dashboardService.create(request);

        assertNotNull(result);
        assertEquals(10, result.getId().intValue());
        assertEquals("New Dashboard", result.getDashboardTitle());
    }

    @Test
    void testGetDashboard() throws Exception {
        String responseBody = "{\n" +
                "  \"id\": 1,\n" +
                "  \"dashboard_title\": \"Test Dashboard\",\n" +
                "  \"description\": \"A test dashboard\"\n" +
                "}";

        mockServer.expect(requestTo(DASHBOARD_PATH + "1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        DashboardDetail result = dashboardService.get(1);

        assertNotNull(result);
        assertEquals(1, result.getId().intValue());
        assertEquals("Test Dashboard", result.getDashboardTitle());
    }

    @Test
    void testUpdateDashboard() throws Exception {
        DashboardCreateRequest request = new DashboardCreateRequest("Updated Dashboard");
        request.setDescription("Updated description");

        String responseBody = "{\n" +
                "  \"id\": 1,\n" +
                "  \"dashboard_title\": \"Updated Dashboard\",\n" +
                "  \"description\": \"Updated description\"\n" +
                "}";

        mockServer.expect(requestTo(DASHBOARD_PATH + "1"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        DashboardDetail result = dashboardService.update(1, request);

        assertNotNull(result);
        assertEquals("Updated Dashboard", result.getDashboardTitle());
        assertEquals("Updated description", result.getDescription());
    }

    @Test
    void testDeleteDashboard() throws Exception {
        mockServer.expect(requestTo(DASHBOARD_PATH + "1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        assertDoesNotThrow(() -> dashboardService.delete(1));
    }

    @Test
    void testListDashboardsWithQueryParams() throws Exception {
        String queryParams = "?q=(page:0,page_size:10)";
        String responseBody = "{\"count\": 0, \"result\": []}";

        mockServer.expect(requestTo(DASHBOARD_PATH + queryParams))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        PaginatedResponse<DashboardInfo> response = dashboardService.list(queryParams);

        assertNotNull(response);
        assertEquals(0, response.getCount());
    }
}
