package productsimulation.request;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SjfPolicyTest {
    @Test
    void testGetRequest() {
        Request.clearIds();
        SjfPolicy policy = new SjfPolicy();
        List<Request> requests = RequestGenerator.generateRequests();
        Request request = policy.getRequest(requests);
        assertEquals(2, request.getId());
    }
}