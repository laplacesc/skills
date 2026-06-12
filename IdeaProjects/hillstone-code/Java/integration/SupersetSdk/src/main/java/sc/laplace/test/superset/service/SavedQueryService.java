package sc.laplace.test.superset.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.*;

/**
 * Service for managing saved SQL queries.
 */
@Service
public class SavedQueryService {

    private static final String PATH = "api/v1/saved_query/";

    private final SupersetClient client;

    public SavedQueryService(SupersetClient client) {
        this.client = client;
    }

    /**
     * List all saved queries.
     */
    public PaginatedResponse<SavedQueryInfo> list() {
        return client.getPaginated(PATH, SavedQueryInfo.class);
    }

    /**
     * List saved queries with query parameters.
     */
    public PaginatedResponse<SavedQueryInfo> list(String queryParams) {
        return client.getPaginated(PATH + queryParams, SavedQueryInfo.class);
    }

    /**
     * Get a single saved query by ID.
     */
    public SavedQueryInfo get(Integer id) {
        return client.get(PATH + id, SavedQueryInfo.class);
    }

    /**
     * Create a new saved query.
     */
    public SavedQueryInfo create(SavedQueryCreateRequest request) {
        return client.post(PATH, request, SavedQueryInfo.class);
    }

    /**
     * Update an existing saved query.
     */
    public SavedQueryInfo update(Integer id, SavedQueryCreateRequest request) {
        return client.put(PATH + id, request, SavedQueryInfo.class);
    }

    /**
     * Delete a saved query.
     */
    public void delete(Integer id) {
        client.delete(PATH + id);
    }

    /**
     * Get saved query data as raw JsonNode.
     */
    public JsonNode getRawData(Integer id) {
        return client.getAsJsonNode(PATH + id);
    }
}
