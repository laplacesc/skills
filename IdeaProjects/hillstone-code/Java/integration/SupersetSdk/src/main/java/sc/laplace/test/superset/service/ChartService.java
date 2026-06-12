package sc.laplace.test.superset.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.*;

/**
 * Service for managing Superset charts (slices).
 */
@Service
public class ChartService {

    private static final String PATH = "api/v1/chart/";

    private final SupersetClient client;

    public ChartService(SupersetClient client) {
        this.client = client;
    }

    /**
     * List all charts.
     */
    public PaginatedResponse<ChartInfo> list() {
        return client.getPaginated(PATH, ChartInfo.class);
    }

    /**
     * List charts with query parameters.
     */
    public PaginatedResponse<ChartInfo> list(String queryParams) {
        return client.getPaginated(PATH + queryParams, ChartInfo.class);
    }

    /**
     * Get a single chart by ID.
     */
    public ChartDetail get(Integer id) {
        return client.get(PATH + id, ChartDetail.class);
    }

    /**
     * Create a new chart.
     */
    public ChartDetail create(ChartCreateRequest request) {
        return client.post(PATH, request, ChartDetail.class);
    }

    /**
     * Update an existing chart.
     */
    public ChartDetail update(Integer id, ChartCreateRequest request) {
        return client.put(PATH + id, request, ChartDetail.class);
    }

    /**
     * Delete a chart.
     */
    public void delete(Integer id) {
        client.delete(PATH + id);
    }

    /**
     * Get chart data via POST /api/v1/chart/data.
     * <p>
     * First fetches the chart detail as raw JSON to extract query_context
     * without SNAKE_CASE interference, then posts it to the chart data endpoint.
     */
    public JsonNode getData(Integer chartId) {
        // 1) Get chart detail as raw JsonNode
        JsonNode detail = client.getAsJsonNode(PATH + chartId);

        // 2) Extract query_context (Superset 4.x) or params (older versions)
        JsonNode queryContext = null;
        if (detail.has("result")) {
            JsonNode result = detail.get("result");
            if (result.has("query_context")) {
                queryContext = result.get("query_context");
            } else if (result.has("params")) {
                // Older Superset: params is a JSON string, need to parse it
                String paramsStr = result.get("params").asText();
                try {
                    queryContext = new com.fasterxml.jackson.databind.ObjectMapper().readTree(paramsStr);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse chart params as JSON", e);
                }
            }
        }

        if (queryContext == null) {
            throw new RuntimeException("Chart " + chartId + " has no query_context or params");
        }

        // 3) Ensure result_format is set (Superset expects this)
        if (queryContext.isObject()) {
            ((com.fasterxml.jackson.databind.node.ObjectNode) queryContext)
                .put("result_format", "json");
        }

        // 4) POST to /api/v1/chart/data to get chart data
        return client.postForJson("api/v1/chart/data", queryContext);
    }

    /**
     * Create an explore permalink for this chart.
     */
    public ExplorePermalinkResponse createPermalink(Integer chartId, ExploreFormDataRequest request) {
        return client.post(PATH + chartId + "/permalink", request, ExplorePermalinkResponse.class);
    }

    /**
     * Get chart data as raw JsonNode.
     */
    public JsonNode getRawData(Integer id) {
        return client.getAsJsonNode(PATH + id);
    }
}
