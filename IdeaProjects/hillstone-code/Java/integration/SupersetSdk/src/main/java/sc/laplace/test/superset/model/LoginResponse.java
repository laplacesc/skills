package sc.laplace.test.superset.model;

/**
 * Login response from Superset security API.
 */
public class LoginResponse {

    private String accessToken;

    public LoginResponse() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
