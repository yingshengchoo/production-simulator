package productsimulation.model.road;

import productsimulation.model.Building;
import productsimulation.request.Request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RequestQueue {
    private static List<Request> queue = new ArrayList<>();

    public static void addRequest(Request request) {
        queue.add(request);
    }

    public static void goOneStep() {
        Iterator<Request> iterator = queue.iterator();

        while (iterator.hasNext()) {

            // 运输延迟-1
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
