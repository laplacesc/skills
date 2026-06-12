package sc.laplace.test.superset.model;

import java.util.List;
import java.util.Map;

/**
 * Request body for creating or updating a dashboard.
 */
public class DashboardCreateRequest {

    private String dashboardTitle;
    private String slug;
    private String description;
    private String css;
    private List<Map<String, Object>> positions;
    private Map<String, Object> metadata;
    private Boolean published;
    private List<Integer> owners;

    public DashboardCreateRequest() {
    }

    public DashboardCreateRequest(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
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

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<Integer> getOwners() {
        return owners;
    }

    public void setOwners(List<Integer> owners) {
        this.owners = owners;
    }
}
