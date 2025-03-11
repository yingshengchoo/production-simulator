package productsimulation.request;

import java.util.List;

public interface ServePolicy {
    /**
     * Retrieves a specific request from the provided list of requests according to a particular selection policy.
     *
     * @param requests the list of requests to process and evaluate
     * @return the selected request based on the implemented serve policy, or null if no suitable request is found
     */
    Request getRequest(List<Request> requests);
}
