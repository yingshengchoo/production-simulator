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
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;


public class BuildingCostHandlerTest {
  @BeforeEach
  void setUp() throws IOException {
    LogicTime.getInstance().reset();
    Board.getBoard().cleanup();
    Building.buildingGlobalList.clear();
    Recipe.recipeGlobalList.clear();
    Request.clearIds();
  }


  @Test
  public void test_constructBuilding() throws Exception {
    Method sendRequest = BuildingCostHandler.class.getDeclaredMethod("sendRequestForBuildingResource");
    sendRequest.setAccessible(true);
    Map<String, Integer> cost = new HashMap<>();
    cost.put("wood", 2);
    //Building s = new Storage();
    BuildingType type = new BuildingType("type", new HashMap<>(), new Cost(cost));
  }

}
