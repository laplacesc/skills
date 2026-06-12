package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Summary info for a chart/slice as returned by the list endpoint.
 */
public class ChartInfo {

    private Integer id;
    private String sliceName;
    private String vizType;
    private String description;
    private String changedByFk;
    private String changedByName;
    private String changedOnDeltaHumanized;
    private String createdByFk;
    private String createdByName;
    private String createdOnDeltaHumanized;
    private String datasourceId;
    private String datasourceType;
    private String datasourceName;
    private String url;
    private String thumbnailUrl;
    private Object formData;
    private List<Map<String, Object>> owners;
    private Boolean isManagedExternially;
    private String certificationDetails;
    private Integer certifiedBy;

    public ChartInfo() {
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

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Object getFormData() {
        return formData;
    }

    public void setFormData(Object formData) {
        this.formData = formData;
    }

    public List<Map<String, Object>> getOwners() {
        return owners;
    }

    public void setOwners(List<Map<String, Object>> owners) {
        this.owners = owners;
    }

    public Boolean getIsManagedExternially() {
        return isManagedExternially;
    }

    public void setIsManagedExternially(Boolean isManagedExternially) {
        this.isManagedExternially = isManagedExternially;
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
}
