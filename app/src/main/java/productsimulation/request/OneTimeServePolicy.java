package productsimulation.request;

import productsimulation.request.servePolicy.ServePolicy;

import java.util.List;

public class OneTimeServePolicy implements ServePolicy {
    @Override
    public Request getRequest(List<Request> requests) {
        return requests.get(0);
    }
}
