package productsimulation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

import org.junit.jupiter.api.Disabled;
import productsimulation.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.*;

class StateTest {

  @TempDir
  File tempDir;
  
  @Test
  public void test_save_and_load() {
    
    File saveDir = new File(tempDir, "SavedStates");
    assertTrue(saveDir.mkdir() || saveDir.exists());
    
    ArrayList<Building> buildings = new ArrayList<>();
    Building mine = new Mine("G", new FactoryType("Gold", Collections.emptyMap()), new ArrayList<>(), null, null);
    buildings.add(mine);
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(mine);
    buildings.add(new Factory("GC", new FactoryType("GoldChain", Collections.emptyMap()), sources, null, null));

    Map<String, Recipe> recipes = new HashMap<>();
    Map<String, Integer> ingredients = new HashMap<>();
    ingredients.put("Egg", 2);
    Recipe eggroll = new Recipe(3, ingredients, "EggRoll");
    recipes.put("EggRoll", eggroll);

    ArrayList<Recipe> stateRecipes = new ArrayList<>();
    stateRecipes.add(eggroll);
    
    ArrayList<FactoryType> types = new ArrayList<>();
    types.add(new FactoryType("EggRoll", recipes));
   
    State state = new State(buildings, types, stateRecipes);
    String filename = "testSave";
    assertDoesNotThrow(() -> state.save(filename));

    File savedFile = new File(filename + ".ser");
    assertTrue(savedFile.exists(), "Saved file does not exist!");

    State loadedState = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    assertDoesNotThrow(() -> loadedState.load(filename));
    
    
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
    void testSavePrintsStackTraceOnIOException() throws IOException {
        State state = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        try (MockedConstruction<FileOutputStream> mockFileOut = mockConstruction(FileOutputStream.class,
                (mock, context) -> {
                    doThrow(new IOException("Mocked IO error")).when(mock).write(any());
                })) {

          assertDoesNotThrow(() ->state.save("testfile"));
        } finally {
            System.setErr(System.err);
        }
    }

  @Test
  public void test_load_exceptions() throws IOException{
     State state = new State(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
  
     File dir = new File("SavedStates");
     if (!dir.exists()) {
        dir.mkdirs();
     }

     assertThrows(IllegalArgumentException.class, () ->state.load("non_existent_file"));
     
     String filename = "invalidObject";
     try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("SavedStates/" + filename + ".ser"))) {
       out.writeObject("This is a string, not a State object"); // Writing incorrect type
     }
     assertThrows(IllegalArgumentException.class, ()-> state.load(filename));
  }
    
}
