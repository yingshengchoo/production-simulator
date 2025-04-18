package productsimulation.model;

import productsimulation.model.Recipe;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildingTypeTest {
  @Test
  public void test_globalBuildingList() {
    ArrayList<BuildingType> bs = new ArrayList<>();
    bs.add(new BuildingType("type1", new HashMap<>()));
    bs.add(new BuildingType("type2", new HashMap<>()));
    BuildingType.buildingTypeGlobalList = new ArrayList<>();

    BuildingType.setBuildingTypeGlobalList(bs);

    assertEquals(bs, BuildingType.getBuildingTypeGlobalList());

    BuildingType bt = new BuildingType("type3", new HashMap<>());
    bt.register();

    assertEquals(bs, BuildingType.getBuildingTypeGlobalList());
    
    BuildingType.buildingTypeGlobalList = new ArrayList<>();    
  }

  @Test
  public void test_getters(){
    Recipe.recipeGlobalList = new ArrayList<>();
    BuildingType type = new BuildingType("type1", new HashMap<>());

    assertEquals("type1", type.getName());
    assertEquals(new HashMap<>(), type.getAllRecipes());

    HashMap<String, Recipe> m = new HashMap<>();
    Recipe r1 = new Recipe(1, new HashMap<>(), "socks");
    m.put("socks", r1);
    HashMap<String, Integer> m2 = new HashMap<>();
    m2.put("cotton", 3);
    m2.put("feet", 2);
    Cost c = new Cost(m2);
    BuildingType type2 = new BuildingType("type2", m, c);
    assertEquals(m, type2.getAllRecipes());
    assertEquals(c, type2.getCost());
    
  }
}
