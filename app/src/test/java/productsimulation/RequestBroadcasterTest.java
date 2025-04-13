package productsimulation;

import static org.junit.jupiter.api.Assertions.*;

import productsimulation.model.*;

import org.junit.jupiter.api.Test;
import java.util.Collections;

public class RequestBroadcasterTest {
  @Test
  public void test_userRequestHandler_illegal() {
    RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
    requestBroadcaster.userRequestHandler("non_exist_item", "non_exist_building");
  }

//  @Test
//  public void test_getters() {
//    RequestBroadcaster rb = RequestBroadcaster.getInstance();
//
//    rb.reset();
//
//    assertEquals(0, rb.getBuildingsSize());
//    assertEquals(0, rb.getRecipesSize());
//    Recipe r1 = new Recipe(1, Collections.emptyMap(), "hotpot");
//    Recipe r2 = new Recipe(3, Collections.emptyMap(), "chocolate");
//    rb.addRecipes(r1);
//    rb.addRecipes(r2);
//    BuildingType t1 = new BuildingType("type1", Collections.emptyMap());
//    Factory f1 =  new Factory("factory1", t1, Collections.emptyList(), null, null);
//    Factory f2 = new Factory("factory2", t1, Collections.emptyList(), null, null);
//    Mine m1 = new Mine("mine1", new BuildingType("minetype", Collections.emptyMap()), null, null);
//    rb.addBuildings(f1);
//    rb.addBuildings(f2);
//    rb.addBuildings(m1);
//
//    assertEquals(2, rb.getRecipesSize());
//    assertEquals(3, rb.getBuildingsSize());
//
//    rb.removeRecipes(r2);
//    rb.removeBuildings(f2);
//    assertEquals(1, rb.getRecipesSize());
//    assertEquals(2, rb.getBuildingsSize());
//
//  }

}
