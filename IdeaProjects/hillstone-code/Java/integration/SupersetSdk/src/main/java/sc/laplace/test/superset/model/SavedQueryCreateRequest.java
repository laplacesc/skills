package sc.laplace.test.superset.model;

/**
 * Request body for creating or updating a saved query.
 */
public class SavedQueryCreateRequest {

    private String dbId;
    private String schema;
    private String description;
    private String label;
    private String sql;
    private String sqlTables;
    private Integer[] owners;

    public SavedQueryCreateRequest() {
    }

    public SavedQueryCreateRequest(String dbId, String label, String sql) {
        this.dbId = dbId;
        this.label = label;
        this.sql = sql;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
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

    public String getSqlTables() {
        return sqlTables;
    }

    public void setSqlTables(String sqlTables) {
        this.sqlTables = sqlTables;
    }

    public Integer[] getOwners() {
        return owners;
    }

    public void setOwners(Integer[] owners) {
        this.owners = owners;
    }
}
