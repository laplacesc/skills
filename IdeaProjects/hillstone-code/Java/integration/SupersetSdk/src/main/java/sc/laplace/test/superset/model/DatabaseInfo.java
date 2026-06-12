package sc.laplace.test.superset.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;

/**
 * Summary info for a database connection as returned by the list endpoint.
 */
public class DatabaseInfo {

    private Integer id;
    private String databaseName;
    private String backend;
    private String sqlalchemyUri;
    private String exposeInSqlLab;
    private String allowDml;
    private String allowCsvUpload;
    private String allowRunAsync;
    private String allowMultiSchemaMetadataFetch;
    private String changedByFk;
    private String changedByName;
    private String changedOnDeltaHumanized;
    private String createdByFk;
    private String createdByName;
    private String createdOnDeltaHumanized;
    private Object engineInformation;
    @JsonDeserialize(using = StringToMapDeserializer.class)
    private Map<String, Object> extra;
    private Boolean isManagedExternally;
    @JsonDeserialize(using = StringToMapDeserializer.class)
    private Map<String, Object> information;

    public DatabaseInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
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

    public Object getEngineInformation() {
        return engineInformation;
    }

    public void setEngineInformation(Object engineInformation) {
        this.engineInformation = engineInformation;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public Boolean getIsManagedExternally() {
        return isManagedExternally;
    }

    public void setIsManagedExternally(Boolean isManagedExternally) {
        this.isManagedExternally = isManagedExternally;
    }

    public Map<String, Object> getInformation() {
        return information;
    }

    public void setInformation(Map<String, Object> information) {
        this.information = information;
    }
}
