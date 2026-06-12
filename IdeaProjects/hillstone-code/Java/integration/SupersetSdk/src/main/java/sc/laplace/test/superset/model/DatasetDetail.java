package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Detailed dataset info.
 */
public class DatasetDetail {

    private Integer id;
    private String tableName;
    private String sql;
    private String description;
    private String schema;
    private String databaseName;
    private Integer databaseId;
    private String kind;
    private String defaultEndpoint;
    private String filterSelectEnabled;
    private String fetchValuesPredicate;
    private String mainDttmCol;
    private Boolean isManagedExternally;
    private String owner;
    private List<Map<String, Object>> owners;
    private List<Map<String, Object>> columns;
    private List<Map<String, Object>> metrics;
    private List<Map<String, Object>> calcMetrics;
    private Map<String, Object> extra;
    private Map<String, Object> data;
    private String changedByFk;
    private String changedByName;
    private String changedOnUtc;
    private String createdByFk;
    private String createdByName;
    private String createdOnUtc;

    public DatasetDetail() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
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

    public Boolean getIsManagedExternally() {
        return isManagedExternally;
    }

    public void setIsManagedExternally(Boolean isManagedExternally) {
        this.isManagedExternally = isManagedExternally;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Map<String, Object>> getOwners() {
        return owners;
    }

    public void setOwners(List<Map<String, Object>> owners) {
        this.owners = owners;
    }

    public List<Map<String, Object>> getColumns() {
        return columns;
    }

    public void setColumns(List<Map<String, Object>> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Map<String, Object>> metrics) {
        this.metrics = metrics;
    }

    public List<Map<String, Object>> getCalcMetrics() {
        return calcMetrics;
    }

    public void setCalcMetrics(List<Map<String, Object>> calcMetrics) {
        this.calcMetrics = calcMetrics;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getChangedByFk() {
        return changedByFk;
    }

    public void setChangedByFk(String changedByFk) {
        this.changedByFk = changedByFk;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }

    public String getChangedOnUtc() {
        return changedOnUtc;
    }

    public void setChangedOnUtc(String changedOnUtc) {
        this.changedOnUtc = changedOnUtc;
    }

    public String getCreatedByFk() {
        return createdByFk;
    }

    public void setCreatedByFk(String createdByFk) {
        this.createdByFk = createdByFk;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedOnUtc() {
        return createdOnUtc;
    }

    public void setCreatedOnUtc(String createdOnUtc) {
        this.createdOnUtc = createdOnUtc;
    }
}
