package productsimulation.request;

import productsimulation.Building;
import productsimulation.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class RequestGenerator {
    public static Request generateRequest(int latency, int[] quantities) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < quantities.length; i++) {
            map.put("ingredient" + i, quantities[i]);
        }
        Recipe recipe = new Recipe(latency, map);
        return new Request("test", recipe, mock(Building.class));
    }

    public static List<Request> generateRequests() {
        List<Request> requests = new ArrayList<>();
        int[] quantities = {1, 2, 3, 4};
        requests.add(generateRequest(10, quantities));
        requests.add(generateRequest(9, quantities));
        requests.add(generateRequest(8, quantities));
        requests.add(generateRequest(8, quantities));
        return requests;
    }
}
