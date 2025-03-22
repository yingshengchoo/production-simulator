package productsimulation.request;

import org.junit.jupiter.api.Test;
import productsimulation.request.servePolicy.FIFOPolicy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FIFOPolicyTest {
    @Test
    void testGetRequest() {
        Request.clearIds();
        FIFOPolicy fifoPolicy = new FIFOPolicy();
        List<Request> requests = RequestGenerator.generateRequests();
        Request request = fifoPolicy.getRequest(requests);
        assertEquals(0, request.getId());
    }
}