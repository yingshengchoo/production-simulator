package productsimulation.request.servePolicy;

import productsimulation.request.Request;
import productsimulation.request.RequestStatus;

import java.util.List;

import java.io.Serializable;

public class ReadyPolicy implements ServePolicy, Serializable {
    /**
     * Selects and returns the request with the smallest ID from the given list of requests
     * that is marked as ready. If no ready requests are found, returns null.
     *
     * @param requests the list of requests to evaluate
     * @return the request with the smallest ID that is ready, or null if no ready requests exist
     */
    @Override
    public Request getRequest(List<Request> requests) {
        int min = Integer.MAX_VALUE;
        Request result = null;
        for(Request request : requests) {
            if (request.getId() < min && request.getStatus() == RequestStatus.READY) {
                min = request.getId();
                result = request;
            }
        }

        return result;
    }
}
