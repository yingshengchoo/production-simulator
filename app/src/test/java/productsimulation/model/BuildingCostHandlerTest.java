package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

public class BuildingCostHandlerTest {
  @Test
  public void test_constructBuilding() throws Exception {
    Method sendRequest = BuildingCostHandler.class.getDeclaredMethod("sendRequestForBuildingResource");
    sendRequest.setAccessible(true);
    Map<String, Integer> cost = new HashMap<>();
    //cost.put();
    BuildingType type = new BuildingType("type", new HashMap<>(), new Cost(cost));
  }

}
