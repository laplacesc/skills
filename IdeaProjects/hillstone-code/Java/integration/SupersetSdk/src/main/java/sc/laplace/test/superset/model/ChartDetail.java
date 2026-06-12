package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Detailed chart/slice info.
 */
public class ChartDetail {

    private Integer id;
    private String sliceName;
    private String vizType;
    private String description;
    private String cacheTimeout;
    private String certificationDetails;
    private Integer certifiedBy;
    private String datasourceId;
    private String datasourceType;
    private Map<String, Object> datasource;
    private Map<String, Object> formData;
    private Map<String, Object> queryContext;
    private List<Map<String, Object>> owners;
    private Map<String, Object> params;
    private Map<String, Object> queryContextGeneration;
    private Boolean isManagedExternally;
    private String changedByFk;
    private String changedByName;
    private String changedOnUtc;
    private String createdByFk;
    private String createdByName;
    private String createdOnUtc;
    private Integer dashboardId;

    public ChartDetail() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSliceName() {
        return sliceName;
    }

    public void setSliceName(String sliceName) {
        this.sliceName = sliceName;
    }

    public String getVizType() {
        return vizType;
    }

    public void setVizType(String vizType) {
        this.vizType = vizType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCacheTimeout() {
        return cacheTimeout;
    }

    public void setCacheTimeout(String cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    public String getCertificationDetails() {
        return certificationDetails;
    }

    public void setCertificationDetails(String certificationDetails) {
        this.certificationDetails = certificationDetails;
    }

    public Integer getCertifiedBy() {
        return certifiedBy;
    }

    public void setCertifiedBy(Integer certifiedBy) {
        this.certifiedBy = certifiedBy;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }

    public Map<String, Object> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, Object> datasource) {
        this.datasource = datasource;
    }

    public Map<String, Object> getFormData() {
        return formData;
    }

    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }

    public Map<String, Object> getQueryContext() {
        return queryContext;
    }

    public void setQueryContext(Map<String, Object> queryContext) {
        this.queryContext = queryContext;
    }

    public List<Map<String, Object>> getOwners() {
        return owners;
    }

    public void setOwners(List<Map<String, Object>> owners) {
        this.owners = owners;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getQueryContextGeneration() {
        return queryContextGeneration;
    }

    public void setQueryContextGeneration(Map<String, Object> queryContextGeneration) {
        this.queryContextGeneration = queryContextGeneration;
    }

    public Boolean getIsManagedExternally() {
        return isManagedExternally;
    }

    public void setIsManagedExternally(Boolean isManagedExternally) {
        this.isManagedExternally = isManagedExternally;
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

    public Integer getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Integer dashboardId) {
        this.dashboardId = dashboardId;
    }
}
