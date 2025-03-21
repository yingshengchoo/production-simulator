package productsimulation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class StateTest {
  @Test
  public void test_save_and_load() {
    ArrayList<Building> buildings = new ArrayList<>();
    Building mine = new Mine("G", "Gold", new ArrayList<>(), null, null);
    buildings.add(mine);
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(mine);
    buildings.add(new Factory("GC", new FactoryType("GoldChain", new ArrayList<>()), sources, null, null));

    ArrayList<Recipe> recipes = new ArrayList<>();
    Map<String, Integer> ingredients = new HashMap<>();
    ingredients.put("Egg", 2);
    Recipe eggroll = new Recipe("EggRoll", 3, ingredients);
    recipes.add(eggroll);
    
    ArrayList<FactoryType> types = new ArrayList<>();
    types.add(new FactoryType("EggRoll", recipes));
   
    State state = new State(buildings, types, recipes);
    String filename = "testSave";
    state.save(filename);
    State loadState = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    loadState.load(filename);

    ByteArrayOutputStream originalOutput = new ByteArrayOutputStream();
    ByteArrayOutputStream loadedOutput = new ByteArrayOutputStream();

    state.showState(new PrintStream(originalOutput));
    loadState.showState(new PrintStream(loadedOutput));

    assertEquals(originalOutput.toString(), loadedOutput.toString());
  }

}
