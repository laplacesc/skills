package sc.laplace.test.superset.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.SqlExecuteRequest;
import sc.laplace.test.superset.model.SqlExecuteResponse;
import sc.laplace.test.superset.model.QueryResult;

/**
 * Service for executing SQL queries via Superset SQL Lab.
 * <p>
 * <b>Note:</b> The connected Superset version uses <b>synchronous mode</b> —
 * query results are returned inline in the {@code execute()} response.
 * The {@code getResult()}, {@code stop()}, and {@code executeSync()} methods
 * depend on independent query endpoints that are not available on this version.
 */
@Service
public class SqlLabService {

    private static final Logger log = LoggerFactory.getLogger(SqlLabService.class);

    private static final String EXECUTE_PATH = "api/v1/sqllab/execute/";
    private static final String RESULT_PATH = "api/v1/query/";
    private static final String STOP_PATH = "api/v1/query/stop/";
    private static final String ESTIMATE_PATH = "api/v1/sqllab/estimate/";

    private final SupersetClient client;

    public SqlLabService(SupersetClient client) {
        this.client = client;
    }

    /**
     * Execute a SQL query in SQL Lab (asynchronous mode).
     *
     * @param request the SQL execution request
     * @return response containing the query ID for status polling
     */
    public SqlExecuteResponse execute(SqlExecuteRequest request) {
        return client.post(EXECUTE_PATH, request, SqlExecuteResponse.class);
    }

    /**
     * Execute a SQL query synchronously (polling until completion).
     *
     * @param databaseId the database ID
     * @param sql        the SQL to execute
     * @param maxWaitMs  maximum time to wait in milliseconds
     * @return the final query result
     * @throws RuntimeException if the query does not complete within maxWaitMs
     */
    public QueryResult executeSync(Integer databaseId, String sql, long maxWaitMs) {
        SqlExecuteRequest request = new SqlExecuteRequest(databaseId, sql);
        request.setRunAsync(true);

        SqlExecuteResponse execResponse = execute(request);
        String queryId = execResponse.getQueryId();

        if (queryId == null) {
            throw new RuntimeException("SQL execution did not return a query ID");
        }

        log.info("SQL query submitted, queryId={}, polling for results", queryId);

        long startTime = System.currentTimeMillis();
        long pollInterval = 1000;

        while (System.currentTimeMillis() - startTime < maxWaitMs) {
            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("SQL query polling interrupted", e);
            }

            QueryResult result = getResult(queryId);
            if (result.isSuccess()) {
                log.info("SQL query completed, queryId={}, rows={}", queryId, result.getRowCount());
                return result;
            } else if (result.isPending()) {
                log.debug("SQL query still running, queryId={}", queryId);
                // Exponential backoff up to 5 seconds
                pollInterval = Math.min(pollInterval * 2, 5000);
            } else {
                log.warn("SQL query failed, queryId={}, error={}", queryId, result.getErrorMessage());
                return result;
            }
        }

        // Timeout — stop the query
        log.warn("SQL query timed out after {}ms, queryId={}", maxWaitMs, queryId);
        stop(queryId);
        throw new RuntimeException("SQL query timed out after " + maxWaitMs + "ms, queryId=" + queryId);
    }

    /**
     * Get the result of a previously executed SQL query.
     * <p>
     * <b>Unavailable:</b> The connected Superset version does not expose
     * an independent query result endpoint. Results are returned inline
     * in the {@link #execute(SqlExecuteRequest)} response.
     */
    public QueryResult getResult(String queryId) {
        return client.get(RESULT_PATH + queryId + "/", QueryResult.class);
    }

    /**
     * Stop a running SQL query.
     * <p>
     * <b>Unavailable:</b> The connected Superset version does not expose
     * a query stop endpoint.
     */
    public JsonNode stop(String queryId) {
        return client.postForJson(STOP_PATH + queryId + "/", new java.util.HashMap<>());
    }

    /**
     * Estimate the number of rows a SQL query would return.
     */
    public JsonNode estimate(SqlExecuteRequest request) {
        return client.postForJson(ESTIMATE_PATH, request);
    }
}
