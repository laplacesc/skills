package sc.laplace.test.superset.model;

import java.util.List;

/**
 * Generic paginated response wrapper for Superset list endpoints.
 * <p>
 * Superset list API returns:
 * <pre>
 * {
 *   "count": 42,
 *   "ids": [1, 2, 3, ...],
 *   "result": [ { ... }, ... ]
 * }
 * </pre>
 *
 * @param <T> the type of elements in the result list
 */
public class PaginatedResponse<T> {

    private long count;
    private List<Integer> ids;
    private List<T> result;

    public PaginatedResponse() {
    }

    public static <T> PaginatedResponse<T> empty() {
        PaginatedResponse<T> pr = new PaginatedResponse<>();
        pr.count = 0;
        return pr;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
