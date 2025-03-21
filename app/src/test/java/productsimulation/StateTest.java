package productsimulation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class StateTest {
  @Test
  public void test_save_and_load() {
    ArrayList<Buildings> buildings = new ArrayList<>();
    Building mine = new Mine("G", "Gold", new ArrayList<>(), new FIFOPolicy(), null);
    buildings.add(mine);
    ArrayList<Buildings> sources = new ArrayList<>();
    sources.add(mine);
    buildings.add(new Factory("GC", "GoldChain", sources, new FIFOPolicy(), null));

    ArrayList<Recipe> recipes = new ArrayList<>();
    Map<String, int> ingredients = new HashMap<>();
    ingredients.put("Egg", 2);
    Recipe eggroll = new Recipe("EggRoll", 3, ingredients);
    recipes.add(eggroll);
    
    ArrayList<FactoryType> types = new ArrayList<>();
    ArrayList<Recipe> recipes = new ArrayList<>();
    recipes.add(eggroll);
    types.add(new FactoryType("EggRoll", recipes));
   
    State state = new State(buildings, types, recipes);
    String filename = "testSave";
    state.save(filename);
    State loadState = new State(new ArrayList<>());
    loadState.load(filename);
    assertEquals(state.toString(), loadState.toString());
  }

}
