package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Detailed dashboard info including all fields.
 */
public class DashboardDetail {

    private Integer id;
    private String dashboardTitle;
    private String slug;
    private String description;
    private String css;
    private String status;
    private Boolean published;
    private List<Map<String, Object>> positions;
    private Map<String, Object> metadata;
    private List<Object> owners;
    private List<Integer> certifiedBy;
    private String certificationDetails;
    private Boolean isManagedExternally;
    private String changedByFk;
    private String changedByName;
    private String changedOnUtc;
    private String createdByFk;
    private String createdByName;
    private String createdOnUtc;
    private List<Integer> charts;
    private List<Integer> datasets;

    public DashboardDetail() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<Map<String, Object>> getPositions() {
        return positions;
    }

    public void setPositions(List<Map<String, Object>> positions) {
        this.positions = positions;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public List<Object> getOwners() {
        return owners;
    }

    public void setOwners(List<Object> owners) {
        this.owners = owners;
    }

    public List<Integer> getCertifiedBy() {
        return certifiedBy;
    }

    public void setCertifiedBy(List<Integer> certifiedBy) {
        this.certifiedBy = certifiedBy;
    }

    public String getCertificationDetails() {
        return certificationDetails;
    }

    public void setCertificationDetails(String certificationDetails) {
        this.certificationDetails = certificationDetails;
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

    public List<Integer> getCharts() {
        return charts;
    }

    public void setCharts(List<Integer> charts) {
        this.charts = charts;
    }

    public List<Integer> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Integer> datasets) {
        this.datasets = datasets;
    }
}
