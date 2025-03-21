package productsimulation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.*;
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
  public void test_IOException_for_load_and_save(){
    State state = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    FileOutputStream fileOutputStreamMock = mock(FileOutputStream.class);
    when(fileOutputStreamMock.write(any(byte[].class))).thenThrow(new IOException("Simulated IO Exception"));
  }

  @Test
  public void test_load_exceptions(){
     State state = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
  
     ObjectInputStream inputStreamMock = mock(ObjectInputStream.class);
     when(inputStreamMock.readObject()).thenThrow(new IOException("Simulated IO Exception"));
        
     ByteArrayOutputStream errContent = new ByteArrayOutputStream();
     System.setErr(new PrintStream(errContent));
        
     state.load("invalid_file");
       
     System.setErr(System.err);
     assertTrue(errContent.toString().contains("java.io.IOException"));
  }
    
}
