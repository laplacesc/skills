package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Summary info for a saved query as returned by the list endpoint.
 */
public class SavedQueryInfo {

    private Integer id;
    private String dbId;
    private String dbName;
    private String schema;
    private String description;
    private String label;
    private String sql;
    private List<Map<String, Object>> sqlTables;
    private String changedByFk;
    private String changedByName;
    private String changedOnDeltaHumanized;
    private String createdByFk;
    private String createdByName;
    private String createdOnDeltaHumanized;
    private String lastRunDeltaHumanized;
    private List<Map<String, Object>> owners;
    private Boolean isManagedExternally;

    public SavedQueryInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Map<String, Object>> getSqlTables() {
        return sqlTables;
    }

    public void setSqlTables(List<Map<String, Object>> sqlTables) {
        this.sqlTables = sqlTables;
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

    public String getLastRunDeltaHumanized() {
        return lastRunDeltaHumanized;
    }

    public void setLastRunDeltaHumanized(String lastRunDeltaHumanized) {
        this.lastRunDeltaHumanized = lastRunDeltaHumanized;
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
