package productsimulation.request.servePolicy;

import productsimulation.request.Request;

import java.util.List;
import java.io.Serializable;
/**
 *
 */
public class FIFOPolicy implements ServePolicy, Serializable {
    /**
     * Selects and returns the request with the smallest ID from the given list of requests.
     * If the list is empty, returns null.
     *
     * @param requests the list of requests to evaluate
     * @return the request with the smallest ID, or null if the list is empty
     */
    @Override
    public Request getRequest(List<Request> requests) {
        int min = Integer.MAX_VALUE;
        Request result = null;
        for(Request request : requests) {
            if (request.getId() < min) {
                min = request.getId();
                result = request;
            }
        }

        return result;
    }

    @Override
    public String getName() {
        return "fifo";
    }
}
