package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Result of a SQL Lab query.
 */
public class QueryResult {

    private String queryId;
    private String status;
    private List<String> columns;
    private List<Map<String, Object>> data;
    private List<List<Object>> values;
    private Long rowCount;
    private String sql;
    private String errorMessage;
    private String startedDttm;
    private String endDttm;

    public QueryResult() {
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

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public List<List<Object>> getValues() {
        return values;
    }

    public void setValues(List<List<Object>> values) {
        this.values = values;
    }

    public Long getRowCount() {
        return rowCount;
    }

    public void setRowCount(Long rowCount) {
        this.rowCount = rowCount;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStartedDttm() {
        return startedDttm;
    }

    public void setStartedDttm(String startedDttm) {
        this.startedDttm = startedDttm;
    }

    public String getEndDttm() {
        return endDttm;
    }

    public void setEndDttm(String endDttm) {
        this.endDttm = endDttm;
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "pending".equalsIgnoreCase(status) || "running".equalsIgnoreCase(status);
    }
}
