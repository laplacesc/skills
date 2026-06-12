package sc.laplace.test.superset.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for executing a SQL query in SQL Lab.
 */
public class SqlExecuteRequest {

    private Integer databaseId;
    private String sql;
    private String schema;
    private String tab;

    @JsonProperty("runAsync")
    private Boolean runAsync;

    private Integer maxRows;

    @JsonProperty("templateParams")
    private String templateParams;

    private Boolean expandData;

    @JsonProperty("queryLimit")
    private String queryLimit;

    @JsonProperty("select_as_cta")
    private Boolean selectAsCreateAs;

    private String createAs;
    private String dataPreview;

    public SqlExecuteRequest() {
    }

    public SqlExecuteRequest(Integer databaseId, String sql) {
        this.databaseId = databaseId;
        this.sql = sql;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
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

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public Boolean getRunAsync() {
        return runAsync;
    }

    public void setRunAsync(Boolean runAsync) {
        this.runAsync = runAsync;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    public String getTemplateParams() {
        return templateParams;
    }

    public void setTemplateParams(String templateParams) {
        this.templateParams = templateParams;
    }

    public Boolean getExpandData() {
        return expandData;
    }

    public void setExpandData(Boolean expandData) {
        this.expandData = expandData;
    }

    public String getQueryLimit() {
        return queryLimit;
    }

    public void setQueryLimit(String queryLimit) {
        this.queryLimit = queryLimit;
    }

    public Boolean getSelectAsCreateAs() {
        return selectAsCreateAs;
    }

    public void setSelectAsCreateAs(Boolean selectAsCreateAs) {
        this.selectAsCreateAs = selectAsCreateAs;
    }

    public String getCreateAs() {
        return createAs;
    }

    public void setCreateAs(String createAs) {
        this.createAs = createAs;
    }

    public String getDataPreview() {
        return dataPreview;
    }

    public void setDataPreview(String dataPreview) {
        this.dataPreview = dataPreview;
    }
}
