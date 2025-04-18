package productsimulation.request;

import org.junit.jupiter.api.Test;

import productsimulation.model.Building;
import productsimulation.model.Recipe;
import java.util.HashMap;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

class RequestTest {
    @Test
    void testUpdateStatus() {
        Request.clearIds();
        Request request = RequestGenerator.generateRequest(10, new int[]{1,2});
        Map<String, Integer> map = new HashMap<>();
        map.put("ingredient0", 1);
        map.put("ingredient1", 4);
        request.updateStatus(map);

        assertEquals(RequestStatus.READY, request.getStatus());

        map.put("ingredient0", 0);
        map.put("ingredient1", 9);
        request.updateStatus(map);

        assertEquals(RequestStatus.WAITING, request.getStatus());
    }

    @Test
    void TestGetLatency() {
        Request.clearIds();
        Request request = RequestGenerator.generateRequest(10, new int[]{1,2});
        assertEquals(10, request.getLatency());
    }

    @Test
    void testGetId() {
        Request.clearIds();
        Request request = RequestGenerator.generateRequest(10, new int[]{1,2});
        assertEquals(0, request.getId());
        request = RequestGenerator.generateRequest(10, new int[]{1,2});
        assertEquals(1, request.getId());
    }

   @Test
   void test_doneReportAndTransport_updates_global_storage(){
     Building.globalStorageMap = new HashMap<>();
     assertEquals(new HashMap<>(), Building.globalStorageMap);
     Request r = new Request("cookie", new Recipe(3, new HashMap<>(), "cookie"), null);
     r.doneReportAndTransport();
     HashMap<String, Integer> m = new HashMap<>();
     m.put("cookie", 1);
     assertEquals(m, Building.globalStorageMap);
     
   }
}
