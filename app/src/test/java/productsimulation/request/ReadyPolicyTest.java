package productsimulation.request;

import org.junit.jupiter.api.Test;
import productsimulation.request.servePolicy.ReadyPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReadyPolicyTest {

    @Test
    void testGetRequest() {
        Request.clearIds();
        ReadyPolicy policy = new ReadyPolicy();
        List<Request> requests = RequestGenerator.generateRequests();
        requests.add(RequestGenerator.generateRequest(10, new int[]{2,1}));
        Map<String, Integer> map = new HashMap<>();
        map.put("ingredient0", 1);
        map.put("ingredient1", 4);
        for (Request req : requests) {
            req.updateStatus(map);
        }
        Request request = policy.getRequest(requests);
        assertNull(request);

        map.put("ingredient0", 4);
        for (Request req : requests) {
            req.updateStatus(map);
        }
        request = policy.getRequest(requests);
        assertEquals(4, request.getId());
    }
}