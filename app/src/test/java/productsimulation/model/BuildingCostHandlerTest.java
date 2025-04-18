package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import productsimulation.command.CommandParser;
import productsimulation.command.RequestCommand;
import productsimulation.command.StepCommand;
import productsimulation.model.*;
import productsimulation.*;
import productsimulation.model.road.Road;
import productsimulation.request.Request;
import productsimulation.request.servePolicy.*;
import productsimulation.request.sourcePolicy.*;
import java.io.*;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;


public class BuildingCostHandlerTest {
  private LogicTime t;
  private Recipe wood;
  private Mine m1;
  private Storage s1;
  private BuildingType type;
  private Factory f;
  @BeforeEach
  void setUp() throws IOException {
    LogicTime.getInstance().reset();
    Board.getBoard().cleanup();
    Building.buildingGlobalList.clear();
    Recipe.recipeGlobalList.clear();
    Request.clearIds();
    setupHelper();
  }

  private void setupHelper(){
    t = LogicTime.getInstance();
    wood = new Recipe(1, Collections.emptyMap(), "wood").register();
    m1 = new Mine("wood", new BuildingType("woodMine", Map.of("wood", wood)) , Collections.emptyList(), new SourceQLen(), new FIFOPolicy(), new Coordinate(1, 2)).register();
    //priority set really low, frequency should be 100, so it shouldn't send any request to mine very often
    s1 = new Storage("WoodStorage", "wood", List.of(m1), 100, 1, new SourceQLen(), new FIFOPolicy(), new Coordinate(1,1)).register();
    //add some wood to storage.
    for(int i = 0; i < 3; i++){
      s1.updateStorage("wood");
    }
    Map<String, Integer> cost = new HashMap<>();
    cost.put("wood", 2);
    type = new BuildingType("type", Map.of("wood", wood), new Cost(cost));
    f = new Factory("Factory", type, List.of(m1, s1),new SourceQLen(), new FIFOPolicy(), new Coordinate(0,1)); //don't register yet as it is still under construction
  }

  @Test
  public void test_constructBuilding() {
    
    BuildingCostHandler.constructBuilding(f);
    //write tests
  }

  @Test  
  public void test_sendRequestForBuildingResource() throws Exception {
    Method method = BuildingCostHandler.class.getDeclaredMethod("sendRequestForBuildingResource", String.class, int.class);
    method.setAccessible(true);
    SourcePolicy policy = new SourceQLen();

    //maake sure setup is as expected
    assertEquals(s1, policy.getSource(Building.buildingGlobalList, "wood"));
    assertEquals(wood, Recipe.getRecipe("wood"));

    method.invoke(null, "wood", 3); //request 2 wood from all buildings. Note: factory also produces wood, but it should not be producing anything during construction.
    
    //Using QLenSourcePolicy should get s1 for both since it has items in the storage .
    //Note: QLen for Storage is -(storge invetory), and it returns request immediately
    assertEquals(0, m1.getRequestCount());
    assertEquals(0, s1.getReqCount()); //storage completes request
    assertEquals(0, GlobalStorage.getItemCount("wood")); //Stilling in readyQueue

    t.stepNHandler(1);  

    assertEquals(3, GlobalStorage.getItemCount("wood")); //Stilling in readyQueue
  
    //Here, mine should be 1 (as storage automatically request from mine since 0%100 = 0
    assertEquals(1, m1.getRequestCount());
    assertEquals(0, s1.getReqCount());
    
    t.stepNHandler(1);  

    method.invoke(null, "wood", 1);
   
    assertEquals(1, m1.getRequestCount());
    assertEquals(0, s1.getReqCount());

    assertEquals(4, GlobalStorage.getItemCount("wood")); //Stilling in readyQueue
    
  }
}
