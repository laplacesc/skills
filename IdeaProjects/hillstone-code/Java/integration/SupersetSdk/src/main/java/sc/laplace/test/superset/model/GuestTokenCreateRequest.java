package sc.laplace.test.superset.model;

import java.util.List;

/**
 * Request body for creating a Superset guest token (POST /api/v1/security/guest_token/).
 * <p>
 * Guest tokens allow embedding Superset dashboards/charts into external applications
 * without requiring the end-user to have a Superset account.
 */
public class GuestTokenCreateRequest {

    /** The user to impersonate (optional — if null, uses the requesting user's identity) */
    private GuestUser user;

    /** Resources to grant access to (e.g. dashboard, chart) */
    private List<GuestResource> resources;

    /** Row-level security filters (optional) */
    private List<GuestRlsRule> rls;

    public GuestTokenCreateRequest() {
    }

    public GuestTokenCreateRequest(List<GuestResource> resources) {
        this.resources = resources;
    }

    public GuestTokenCreateRequest(GuestUser user, List<GuestResource> resources, List<GuestRlsRule> rls) {
        this.user = user;
        this.resources = resources;
        this.rls = rls;
    }

    public GuestUser getUser() {
        return user;
    }

    public void setUser(GuestUser user) {
        this.user = user;
    }

    public List<GuestResource> getResources() {
        return resources;
    }

    public void setResources(List<GuestResource> resources) {
        this.resources = resources;
    }

    public List<GuestRlsRule> getRls() {
        return rls;
    }

    public void setRls(List<GuestRlsRule> rls) {
        this.rls = rls;
    }

    // ============================================================
    // Embedded types
    // ============================================================

    /**
     * Guest user identity.
     */
    public static class GuestUser {
        private String username;
        private String firstName;
        private String lastName;

        public GuestUser() {
        }

        public GuestUser(String username, String firstName, String lastName) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    /**
     * A resource to grant access to (dashboard, chart, etc.).
     */
    public static class GuestResource {
        private String type;
        private String id;

        public GuestResource() {
        }

        public GuestResource(String type, String id) {
            this.type = type;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    /**
     * Row-level security rule.
     */
    public static class GuestRlsRule {
        private String clause;

        public GuestRlsRule() {
        }

        public GuestRlsRule(String clause) {
            this.clause = clause;
        }

        public String getClause() {
            return clause;
        }

        public void setClause(String clause) {
            this.clause = clause;
        }
    }
}
