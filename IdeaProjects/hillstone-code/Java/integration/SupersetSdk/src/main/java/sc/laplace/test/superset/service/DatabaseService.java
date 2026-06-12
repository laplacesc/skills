package sc.laplace.test.superset.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.*;


/**
 * Service for managing Superset database connections.
 */
@Service
public class DatabaseService {

    private static final String PATH = "api/v1/database/";

    private final SupersetClient client;

    public DatabaseService(SupersetClient client) {
        this.client = client;
    }

    /**
     * List all database connections.
     */
    public PaginatedResponse<DatabaseInfo> list() {
        return client.getPaginated(PATH, DatabaseInfo.class);
    }

    /**
     * List databases with query parameters.
     */
    public PaginatedResponse<DatabaseInfo> list(String queryParams) {
        return client.getPaginated(PATH + queryParams, DatabaseInfo.class);
    }

    /**
     * Get a single database connection by ID.
     */
    public DatabaseInfo get(Integer id) {
        return client.get(PATH + id, DatabaseInfo.class);
    }

    /**
     * Create a new database connection.
     */
    public DatabaseInfo create(DatabaseCreateRequest request) {
        return client.post(PATH, request, DatabaseInfo.class);
    }

    /**
     * Update an existing database connection.
     */
    public DatabaseInfo update(Integer id, DatabaseCreateRequest request) {
        return client.put(PATH + id, request, DatabaseInfo.class);
    }

    /**
     * Delete a database connection.
     */
    public void delete(Integer id) {
        client.delete(PATH + id);
    }

    /**
     * Test a database connection.
     */
    public JsonNode testConnection(DatabaseCreateRequest request) {
        return client.postForJson(PATH + "test_connection", request);
    }

    /**
     * Get database schemas.
     */
    public JsonNode getSchemas(Integer databaseId) {
        return client.getAsJsonNode(PATH + databaseId + "/schemas/");
    }

    /**
     * Get tables for a database schema.
     */
    public JsonNode getTables(Integer databaseId, String schema) {
        // Rison format expected by Superset: (schema_name:value)
        String risonQuery = "(schema_name:" + schema + ")";
        return client.getAsJsonNode(PATH + databaseId + "/tables/?q=" + risonQuery);
    }

    /**
     * Validate SQL against this database.
     */
    public JsonNode validateSql(Integer databaseId, String sql) {
        return client.postForJson(PATH + databaseId + "/sql_valid/", new SqlValidationRequest(sql));
    }

    /**
     * Get database connection data as raw JsonNode.
     */
    public JsonNode getRawData(Integer id) {
        return client.getAsJsonNode(PATH + id);
    }

    /**
     * Simple inner class for SQL validation requests.
     */
    private static class SqlValidationRequest {
        @SuppressWarnings("unused")
        public final String sql;

        SqlValidationRequest(String sql) {
            this.sql = sql;
        }
    }
}
