package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Summary info for a dataset as returned by the list endpoint.
 */
public class DatasetInfo {

    private Integer id;
    private String tableName;
    private String sql;
    private String description;
    private String filterSelectEnabled;
    private String fetchValuesPredicate;
    private String schema;
    private String databaseName;
    private Integer databaseId;
    private String changedByFk;
    private String changedByName;
    private String changedOnDeltaHumanized;
    private String createdByFk;
    private String createdByName;
    private String createdOnDeltaHumanized;
    private String defaultEndpoint;
    private String exploreUrl;
    private String kind;
    private String owner;
    private List<Map<String, Object>> owners;
    private Boolean isManagedExternally;

    public DatasetInfo() {
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

    public String getChangedOnDeltaHumanized() {
        return changedOnDeltaHumanized;
    }

    public void setChangedOnDeltaHumanized(String changedOnDeltaHumanized) {
        this.changedOnDeltaHumanized = changedOnDeltaHumanized;
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

    public String getCreatedOnDeltaHumanized() {
        return createdOnDeltaHumanized;
    }

    public void setCreatedOnDeltaHumanized(String createdOnDeltaHumanized) {
        this.createdOnDeltaHumanized = createdOnDeltaHumanized;
    }

    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }

    public void setDefaultEndpoint(String defaultEndpoint) {
        this.defaultEndpoint = defaultEndpoint;
    }

    public String getExploreUrl() {
        return exploreUrl;
    }

    public void setExploreUrl(String exploreUrl) {
        this.exploreUrl = exploreUrl;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
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

    public Boolean getIsManagedExternally() {
        return isManagedExternally;
    }

    public void setIsManagedExternally(Boolean isManagedExternally) {
        this.isManagedExternally = isManagedExternally;
    }
}
