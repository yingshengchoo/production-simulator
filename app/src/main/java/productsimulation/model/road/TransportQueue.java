package productsimulation.model.road;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.model.Building;
import productsimulation.model.waste.WasteDisposal;
import productsimulation.request.Request;
import productsimulation.request.WasteRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.Serializable;

public class TransportQueue implements Serializable {
    public static List<Request> queue = new ArrayList<>();

    public static void addRequest(Request request) {
        queue.add(request);
    }

    public static void goOneStep() {
        Iterator<Request> iterator = queue.iterator();

        while (iterator.hasNext()) {

            // 运输延迟-1
            Request request = iterator.next();
            Log.debugLog(request.getIngredient() + " decrease from " + request.transLatency +
                    " at " + LogicTime.getInstance().getStep());
            request.decreaseTransLatency();

            if (request.isReadyToDeliver()) {
                // deliver request!
                Building requester = request.getRequester();

                if (request.getClass().equals(WasteRequest.class)) {
                    // 提交waste
                    WasteRequest wasteRequest = (WasteRequest) request;
                    WasteDisposal wasteDisposal = (WasteDisposal) requester;
                    wasteDisposal.commitWaste(wasteRequest.getIngredient(), wasteRequest.getCount());
                } else {
                    // 提交普通request
                    requester.updateStorage(request.getIngredient());
                }

                iterator.remove();
            }
        }
    }
}
