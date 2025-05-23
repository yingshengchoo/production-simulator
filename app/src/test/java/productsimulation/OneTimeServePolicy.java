package productsimulation;

import productsimulation.request.Request;
import productsimulation.request.servePolicy.ServePolicy;
import java.io.Serializable;
import java.util.List;

public class OneTimeServePolicy implements ServePolicy, Serializable {
    @Override
    public Request getRequest(List<Request> requests) {
        return requests.get(0);
    }

    @Override
    public String getName() {
        return "OneTimeServePolicy";
    }
}
