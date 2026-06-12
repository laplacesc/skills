package sc.laplace.test.superset.service;

import org.springframework.stereotype.Service;
import sc.laplace.test.superset.client.SupersetClient;
import sc.laplace.test.superset.model.GuestTokenCreateRequest;
import sc.laplace.test.superset.model.GuestTokenCreateRequest.GuestResource;
import sc.laplace.test.superset.model.GuestTokenCreateRequest.GuestUser;
import sc.laplace.test.superset.model.GuestTokenCreateRequest.GuestRlsRule;
import sc.laplace.test.superset.model.GuestTokenResponse;

import java.util.Collections;
import java.util.List;

/**
 * Service for creating Superset guest tokens used for embedding
 * dashboards and charts into external applications.
 * <p>
 * Guest tokens allow embedding Superset resources without requiring
 * the end-user to log in to Superset.
 */
@Service
public class GuestTokenService {

    private static final String PATH = "api/v1/security/guest_token/";

    private final SupersetClient client;

    public GuestTokenService(SupersetClient client) {
        this.client = client;
    }

    /**
     * Create a guest token for the given resources.
     *
     * @param resources one or more resources (dashboards, charts) to grant access to
     * @return the guest token response
     */
    public GuestTokenResponse createToken(List<GuestResource> resources) {
        return createToken(null, resources, null);
    }

    /**
     * Create a guest token for a specific dashboard.
     *
     * @param dashboardId the dashboard ID to embed
     * @return the guest token response
     */
    public GuestTokenResponse createDashboardToken(Integer dashboardId) {
        GuestResource resource = new GuestResource("dashboard", String.valueOf(dashboardId));
        return createToken(null, Collections.singletonList(resource), null);
    }

    /**
     * Create a guest token with full control over user, resources, and RLS rules.
     *
     * @param user      optional user identity to impersonate
     * @param resources resources to grant access to
     * @param rls       optional row-level security rules
     * @return the guest token response
     */
    public GuestTokenResponse createToken(GuestUser user, List<GuestResource> resources, List<GuestRlsRule> rls) {
        GuestTokenCreateRequest request = new GuestTokenCreateRequest(user, resources, rls);
        return client.createGuestToken(request);
    }

    /**
     * Get the underlying SupersetClient for advanced usage.
     */
    public SupersetClient getClient() {
        return client;
    }
}
