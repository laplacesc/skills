package sc.laplace.test.superset.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.*;

import java.util.List;

/**
 * Service for managing Superset dashboards.
 */
@Service
public class DashboardService {

    private static final String PATH = "api/v1/dashboard/";

    private final SupersetClient client;

    public DashboardService(SupersetClient client) {
        this.client = client;
    }

    /**
     * List all dashboards.
     */
    public PaginatedResponse<DashboardInfo> list() {
        return client.getPaginated(PATH, DashboardInfo.class);
    }

    /**
     * List dashboards with query parameters (e.g. "?q=(page:0,page_size:20)").
     */
    public PaginatedResponse<DashboardInfo> list(String queryParams) {
        return client.getPaginated(PATH + queryParams, DashboardInfo.class);
    }

    /**
     * Get a single dashboard by ID.
     */
    public DashboardDetail get(Integer id) {
        return client.get(PATH + id, DashboardDetail.class);
    }

    /**
     * Create a new dashboard.
     */
    public DashboardDetail create(DashboardCreateRequest request) {
        return client.post(PATH, request, DashboardDetail.class);
    }

    /**
     * Update an existing dashboard.
     */
    public DashboardDetail update(Integer id, DashboardCreateRequest request) {
        return client.put(PATH + id, request, DashboardDetail.class);
    }

    /**
     * Delete a dashboard.
     */
    public void delete(Integer id) {
        client.delete(PATH + id);
    }

    /**
     * Get all charts belonging to a dashboard.
     */
    public List<ChartInfo> getCharts(Integer dashboardId) {
        return client.getList(PATH + dashboardId + "/charts", ChartInfo.class);
    }

    /**
     * Export dashboard as a JSON/YAML file (binary download).
     */
    public byte[] export(Integer id) {
        return client.download(PATH + "export/" + id);
    }

    /**
     * Get dashboard data as raw JsonNode.
     */
    public JsonNode getData(Integer id) {
        return client.getAsJsonNode(PATH + id);
    }
}
