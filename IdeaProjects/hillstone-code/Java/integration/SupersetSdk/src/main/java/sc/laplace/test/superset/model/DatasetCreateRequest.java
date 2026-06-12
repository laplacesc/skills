package sc.laplace.test.superset.model;

import java.util.List;

/**
 * Request body for creating or updating a dataset.
 */
public class DatasetCreateRequest {

    private String tableName;
    private String sql;
    private String description;
    private String schema;
    private Integer databaseId;
    private String kind;
    private String defaultEndpoint;
    private String filterSelectEnabled;
    private String fetchValuesPredicate;
    private String mainDttmCol;
    private List<Integer> owners;

    public DatasetCreateRequest() {
    }

    public DatasetCreateRequest(String tableName, Integer databaseId) {
        this.tableName = tableName;
        this.databaseId = databaseId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }

    public void setDefaultEndpoint(String defaultEndpoint) {
        this.defaultEndpoint = defaultEndpoint;
    }

    public String getFilterSelectEnabled() {
        return filterSelectEnabled;
    }

    public void setFilterSelectEnabled(String filterSelectEnabled) {
        this.filterSelectEnabled = filterSelectEnabled;
    }

    public String getFetchValuesPredicate() {
        return fetchValuesPredicate;
    }

    public void setFetchValuesPredicate(String fetchValuesPredicate) {
        this.fetchValuesPredicate = fetchValuesPredicate;
    }

    public String getMainDttmCol() {
        return mainDttmCol;
    }

    public void setMainDttmCol(String mainDttmCol) {
        this.mainDttmCol = mainDttmCol;
    }

    public List<Integer> getOwners() {
        return owners;
    }

    public void setOwners(List<Integer> owners) {
        this.owners = owners;
    }
}
