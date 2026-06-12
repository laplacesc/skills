package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Summary info for a dashboard as returned by the list endpoint.
 */
public class DashboardInfo {

    private Integer id;
    private String dashboardTitle;
    private String slug;
    private String status;
    private String published;
    private String changedByFk;
    private String changedByName;
    private String changedOnDeltaHumanized;
    private String createdByFk;
    private String createdByName;
    private String createdOnDeltaHumanized;
    private String url;
    private String thumbnailUrl;
    private Boolean isManagedExternally;
    private List<Map<String, Object>> owners;

    public DashboardInfo() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
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

    public Boolean getIsManagedExternally() {
        return isManagedExternally;
    }

    public void setIsManagedExternally(Boolean isManagedExternally) {
        this.isManagedExternally = isManagedExternally;
    }

    public List<Map<String, Object>> getOwners() {
        return owners;
    }

    public void setOwners(List<Map<String, Object>> owners) {
        this.owners = owners;
    }
}
