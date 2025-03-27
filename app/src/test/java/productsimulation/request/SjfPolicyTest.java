package productsimulation.request;

import org.junit.jupiter.api.Test;
import productsimulation.request.servePolicy.SjfPolicy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SjfPolicyTest {
    @Test
    void testGetRequest() {
        Request.clearIds();
        SjfPolicy policy = new SjfPolicy();
        List<Request> requests = RequestGenerator.generateRequests();
        Request request = policy.getRequest(requests);
        assertEquals(1, request.getId());

        requests.add(0, RequestGenerator.generateRequest(6, new int[]{1,2}));
        requests.add(RequestGenerator.generateRequest(6, new int[]{1,2}));
        request = policy.getRequest(requests);
        assertEquals(1, request.getId());
    }

    @Test
    void testGetName() {
        SjfPolicy policy = new SjfPolicy();
        assertEquals("sjf", policy.getName());
    }
}