package sc.laplace.test.superset.exception;

/**
 * Exception thrown when a Superset API call fails.
 */
public class SupersetApiException extends RuntimeException {

    private final int statusCode;
    private final String apiPath;

    public SupersetApiException(int statusCode, String apiPath, String message) {
        super(formatMessage(statusCode, apiPath, message));
        this.statusCode = statusCode;
        this.apiPath = apiPath;
    }

    public SupersetApiException(int statusCode, String apiPath, String message, Throwable cause) {
        super(formatMessage(statusCode, apiPath, message), cause);
        this.statusCode = statusCode;
        this.apiPath = apiPath;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getApiPath() {
        return apiPath;
    }

    private static String formatMessage(int statusCode, String apiPath, String message) {
        return String.format("Superset API error [%d] %s: %s", statusCode, apiPath, message);
    }
}
