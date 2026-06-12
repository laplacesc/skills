package sc.laplace.test.superset.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.*;

/**
 * Service for managing Superset datasets.
 */
@Service
public class DatasetService {

    private static final String PATH = "api/v1/dataset/";

    private final SupersetClient client;

    public DatasetService(SupersetClient client) {
        this.client = client;
    }

    /**
     * List all datasets.
     */
    public PaginatedResponse<DatasetInfo> list() {
        return client.getPaginated(PATH, DatasetInfo.class);
    }

    /**
     * List datasets with query parameters.
     */
    public PaginatedResponse<DatasetInfo> list(String queryParams) {
        return client.getPaginated(PATH + queryParams, DatasetInfo.class);
    }

    /**
     * Get a single dataset by ID.
     */
    public DatasetDetail get(Integer id) {
        return client.get(PATH + id, DatasetDetail.class);
    }

    /**
     * Create a new dataset.
     */
    public DatasetDetail create(DatasetCreateRequest request) {
        return client.post(PATH, request, DatasetDetail.class);
    }

    /**
     * Update an existing dataset.
     */
    public DatasetDetail update(Integer id, DatasetCreateRequest request) {
        return client.put(PATH + id, request, DatasetDetail.class);
    }

    /**
     * Delete a dataset.
     */
    public void delete(Integer id) {
        client.delete(PATH + id);
    }

    /**
     * Refresh the schema of a dataset (sync columns/metrics).
     */
    public JsonNode refreshSchema(Integer datasetId) {
        return client.postForJson(PATH + datasetId + "/refresh", null);
    }

    /**
     * Get dataset data as raw JsonNode.
     */
    public JsonNode getRawData(Integer id) {
        return client.getAsJsonNode(PATH + id);
    }
}
