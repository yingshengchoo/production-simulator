package productsimulation.request.servePolicy;

import productsimulation.request.Request;

import java.util.List;

public class SjfPolicy implements ServePolicy {

    /**
     * Selects and returns the request with the smallest latency from the given list of requests.
     * In the case of a tie, selects the request with the smallest ID among those with the smallest latency.
     * If the list is empty, returns null.
     *
     * @param requests the list of requests to evaluate
     * @return the request with the smallest latency, or the request with the smallest ID among those
     *         with the smallest latency in case of a tie; returns null if the list is empty
     */
    @Override
    public Request getRequest(List<Request> requests) {
        int min = Integer.MAX_VALUE;
        Request result = null;
        for(Request request : requests) {
            if (request.getLatency() < min) {
                min = request.getLatency();
                result = request;
            } else if (request.getLatency() == min &&
                    result != null &&
                    request.getId() < result.getId()) {
                result = request;
            }
        }

        return result;
    }
}
