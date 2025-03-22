package productsimulation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

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

  @Test
  public void test_checkFilename(){
    State state = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    assertTrue(state.checkFilename("normalfilename"));
    assertFalse(state.checkFilename("illegal:name"));
    assertFalse(state.checkFilename(null));
    assertFalse(state.checkFilename(""));
  }

  @Test
  public void testSave_InvalidFilename() {
    State state = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    assertThrows(IllegalArgumentException.class, () -> state.save("invalid/file"));
  }

  @Test
  public void test_save_exceptions(){
    State state = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    String validFilename = "testState";
    File directory = new File("SavedStates");
    directory.setReadOnly();

    try {
       assertDoesNotThrow(() -> state.save(validFilename)); 
    } finally {
       directory.setWritable(true); 
    }
  }

  @Test
  public void test_load_exceptions(){
     State state = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
  
     assertThrows(IllegalArgumentException.class, () ->state.load("non_existent_file"));

     String filename = "invalidObject";
     try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("SavedStates/" + filename + ".ser"))) {
       out.writeObject("This is a string, not a State object"); // Writing incorrect type
     }
     assertThrows(IllegalArgumentException.class, ()->{
            new YourClass().load(filename);
        });
  }
    
}
