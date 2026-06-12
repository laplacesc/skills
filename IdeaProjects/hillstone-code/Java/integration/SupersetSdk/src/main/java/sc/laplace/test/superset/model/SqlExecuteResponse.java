package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Response from executing a SQL query in SQL Lab.
 */
public class SqlExecuteResponse {

    private String queryId;
    private String status;
    private String sql;
    private String schema;
    private Integer databaseId;
    private String tab;
    private String endDttm;
    private String errorMessage;
    private String resultsKey;
    private String startDttm;
    private String temporaryCache;
    private String trackingUrl;
    private List<Map<String, Object>> data;
    private List<Map<String, Object>> columns;
    private Long rowCount;

    public SqlExecuteResponse() {
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getEndDttm() {
        return endDttm;
    }

    public void setEndDttm(String endDttm) {
        this.endDttm = endDttm;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResultsKey() {
        return resultsKey;
    }

    public void setResultsKey(String resultsKey) {
        this.resultsKey = resultsKey;
    }

    public String getStartDttm() {
        return startDttm;
    }

    public void setStartDttm(String startDttm) {
        this.startDttm = startDttm;
    }

    public String getTemporaryCache() {
        return temporaryCache;
    }

    public void setTemporaryCache(String temporaryCache) {
        this.temporaryCache = temporaryCache;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public List<Map<String, Object>> getColumns() {
        return columns;
    }

    public void setColumns(List<Map<String, Object>> columns) {
        this.columns = columns;
    }

    public Long getRowCount() {
        return rowCount;
    }

    public void setRowCount(Long rowCount) {
        this.rowCount = rowCount;
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "pending".equalsIgnoreCase(status) || "running".equalsIgnoreCase(status);
    }

    public boolean isFailed() {
        return "failed".equalsIgnoreCase(status) || "error".equalsIgnoreCase(status);
    }
}
