package sc.laplace.test.superset.model;

import java.util.Map;

/**
 * Request body for creating or testing a database connection.
 */
public class DatabaseCreateRequest {

    private String databaseName;
    private String sqlalchemyUri;
    private String exposeInSqlLab;
    private String allowDml;
    private String allowCsvUpload;
    private String allowRunAsync;
    private String allowMultiSchemaMetadataFetch;
    private Boolean isManagedExternally;
    private Map<String, Object> extra;
    private Map<String, Object> encryptedExtra;
    private Integer[] owners;

    public DatabaseCreateRequest() {
    }

    public DatabaseCreateRequest(String databaseName, String sqlalchemyUri) {
        this.databaseName = databaseName;
        this.sqlalchemyUri = sqlalchemyUri;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getSqlalchemyUri() {
        return sqlalchemyUri;
    }

    public void setSqlalchemyUri(String sqlalchemyUri) {
        this.sqlalchemyUri = sqlalchemyUri;
    }

    public String getExposeInSqlLab() {
        return exposeInSqlLab;
    }

    public void setExposeInSqlLab(String exposeInSqlLab) {
        this.exposeInSqlLab = exposeInSqlLab;
    }

    public String getAllowDml() {
        return allowDml;
    }

    public void setAllowDml(String allowDml) {
        this.allowDml = allowDml;
    }

    public String getAllowCsvUpload() {
        return allowCsvUpload;
    }

    public void setAllowCsvUpload(String allowCsvUpload) {
        this.allowCsvUpload = allowCsvUpload;
    }

    public String getAllowRunAsync() {
        return allowRunAsync;
    }

    public void setAllowRunAsync(String allowRunAsync) {
        this.allowRunAsync = allowRunAsync;
    }

    public String getAllowMultiSchemaMetadataFetch() {
        return allowMultiSchemaMetadataFetch;
    }

    public void setAllowMultiSchemaMetadataFetch(String allowMultiSchemaMetadataFetch) {
        this.allowMultiSchemaMetadataFetch = allowMultiSchemaMetadataFetch;
    }

    public Boolean getIsManagedExternally() {
        return isManagedExternally;
    }

    public void setIsManagedExternally(Boolean isManagedExternally) {
        this.isManagedExternally = isManagedExternally;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public Map<String, Object> getEncryptedExtra() {
        return encryptedExtra;
    }

    public void setEncryptedExtra(Map<String, Object> encryptedExtra) {
        this.encryptedExtra = encryptedExtra;
    }

    public Integer[] getOwners() {
        return owners;
    }

    public void setOwners(Integer[] owners) {
        this.owners = owners;
    }
}
