package sc.laplace.test.superset.model;

import java.util.Map;

/**
 * Request body for creating or updating a chart/slice.
 */
public class ChartCreateRequest {

    private String sliceName;
    private String vizType;
    private String description;
    private String datasourceId;
    private String datasourceType;
    private Map<String, Object> params;
    private Map<String, Object> formData;
    private Map<String, Object> queryContext;
    private String cacheTimeout;
    private String certificationDetails;
    private Integer certifiedBy;
    private Integer dashboardId;

    public ChartCreateRequest() {
    }

    public ChartCreateRequest(String sliceName, String vizType, String datasourceId, String datasourceType) {
        this.sliceName = sliceName;
        this.vizType = vizType;
        this.datasourceId = datasourceId;
        this.datasourceType = datasourceType;
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

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
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

    public Integer getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Integer dashboardId) {
        this.dashboardId = dashboardId;
    }
}
