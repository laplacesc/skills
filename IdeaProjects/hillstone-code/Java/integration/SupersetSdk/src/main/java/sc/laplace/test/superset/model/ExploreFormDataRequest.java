package sc.laplace.test.superset.model;

import java.util.Map;

/**
 * Request body for explore form data operations.
 */
public class ExploreFormDataRequest {

    private String datasourceId;
    private String datasourceType;
    private String vizType;
    private Map<String, Object> formData;

    public ExploreFormDataRequest() {
    }

    public ExploreFormDataRequest(String datasourceId, String datasourceType, Map<String, Object> formData) {
        this.datasourceId = datasourceId;
        this.datasourceType = datasourceType;
        this.formData = formData;
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

    public String getVizType() {
        return vizType;
    }

    public void setVizType(String vizType) {
        this.vizType = vizType;
    }

    public Map<String, Object> getFormData() {
        return formData;
    }

    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }
}
