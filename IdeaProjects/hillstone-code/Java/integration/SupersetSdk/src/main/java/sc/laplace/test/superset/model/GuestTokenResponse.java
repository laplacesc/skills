package sc.laplace.test.superset.model;

/**
 * Response from the Superset guest token API (POST /api/v1/security/guest_token/).
 */
public class GuestTokenResponse {

    /** The JWT guest token used for embedding Superset resources */
    private String token;

    /** Optional URL for embedding (some Superset versions include this) */
    private String url;

    public GuestTokenResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
