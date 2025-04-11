package productsimulation.model;

import productsimulation.request.Request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RequestQueue {
    private List<Request> queue;

    public RequestQueue() {
        this.queue = new ArrayList<>();
    }

    public void addRequest(Request request) {
        queue.add(request);
    }

    public void decrementGlobalTime() {
        Iterator<Request> iterator = queue.iterator();

        while (iterator.hasNext()) {
            Request request = iterator.next();
            request.decreaseTransLatency();

            if (request.isReadyToDeliver()) {
                // deliver request!
                Building requester = request.getRequester();
                requester.updateStorage(request.getIngredient());

                iterator.remove();
            }
        }
    }
}
