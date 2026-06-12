package sc.laplace.test.superset.model;

/**
 * Response from creating an explore permalink.
 */
public class ExplorePermalinkResponse {

    private String url;
    private String key;

    public ExplorePermalinkResponse() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
