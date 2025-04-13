package productsimulation;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

import productsimulation.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;

class StateTest {

  @BeforeEach
  public void resetState() {
    Board.getBoard().cleanup();
    Building.buildingGlobalList.clear();
    try{
      State.getInstance().reset();
      State.getInstance().setInstanceToNull();
    } catch(IllegalStateException e){
      //do nothing, only reset if needed.
    }
  }

  @Test
  public void test_state_singleton(){
    State.initialize(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), null, null);
    State s1 = State.getInstance();
    s1.reset();
    State s2 = State.getInstance();
    assertEquals(s1, s2);
    ArrayList<Building> buildings = new ArrayList<>();
    State.initialize(buildings, new ArrayList<>(), new ArrayList<>(), null, null);
    State s3 = State.getInstance();
    assertEquals(s1,s3);

  }

  @Test
  public void test_save_and_load() {
    ArrayList<Building> buildings = new ArrayList<>();
    Building mine = new Mine("G", new BuildingType("Gold", Collections.emptyMap()), new ArrayList<>(), null, null);
    buildings.add(mine);
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(mine);
    Building factory = new Factory("GC", new BuildingType("GoldChain", Collections.emptyMap()), sources, null, null);
    buildings.add(factory);

    Map<String, Recipe> recipes = new HashMap<>();
    Map<String, Integer> ingredients = new HashMap<>();
    ingredients.put("Egg", 2);
    Recipe eggroll = new Recipe(3, ingredients, "EggRoll");
    recipes.put("EggRoll", eggroll);

    ArrayList<Recipe> stateRecipes = new ArrayList<>();
    stateRecipes.add(eggroll);
    
    ArrayList<BuildingType> types = new ArrayList<>();
    types.add(new BuildingType("EggRoll", recipes));

    RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
//    requestBroadcaster.addRecipes(eggroll);
//    requestBroadcaster.addBuildings(mine);
//    requestBroadcaster.addBuildings(factory);

    LogicTime logicTime = LogicTime.getInstance();
//    logicTime.addObservers(mine);
//    logicTime.addObservers(factory);
    
    State.initialize(buildings, types, stateRecipes, requestBroadcaster, logicTime);

    State state = State.getInstance();
    
    String filename = "testSave";
    assertDoesNotThrow(() -> state.save(filename));

    File file = new File("SavedStates/" + filename + ".ser");
    assertTrue(file.exists(), "File should exist after saving state.");

    ByteArrayOutputStream originalOutput = new ByteArrayOutputStream();
    state.showState(new PrintStream(originalOutput));
    
    String expected = "Current State Information:\n" +
    "Current Step: 0\n" + 
    "Recipes:\n" + 
    "Recipe\n" +
    "{output='EggRoll',\n" +
    " ingredients={Egg=2},\n" +
    " latency=3\n" + 
    "}\n" +
    "Factory Types:\n" + 
    "Factory Type\n" +  
    "{name='EggRoll',\n" + 
    " recipes=[EggRoll]\n" +
    "}\n" + 
    "Buildings:\n" +
    "Mine\n" + 
    "{name='G',\n" +
    " mine='Gold',\n" +
    " sources=[],\n" + 
    " storage=[],\n" +
    " request queue size=0\n" +
    "}\n" +
    "Factory\n" +
    "{name='GC',\n" +
    " type='GoldChain',\n" +
    " sources=[G],\n" +
    " storage=[],\n" +
    " request queue size=0\n" +
    "}\n";
    
    assertEquals(expected, originalOutput.toString());
    
    state.reset();

    assertDoesNotThrow(() -> state.load(filename));
    
    ByteArrayOutputStream loadedOutput = new ByteArrayOutputStream();
    state.showState(new PrintStream(loadedOutput));

    assertEquals(originalOutput.toString(), loadedOutput.toString());
  }


  @Test
  public void test_checkFilename(){
    State.initialize(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), null, null);
    State state = State.getInstance();
    assertTrue(state.checkFilename("normalfilename"));
    assertFalse(state.checkFilename("illegal:name"));
    assertFalse(state.checkFilename(null));
    assertFalse(state.checkFilename(""));
  }

  @Test
  public void testSave_InvalidFilename() {
    State.initialize(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), null, null);
    assertThrows(IllegalArgumentException.class, () -> State.getInstance().save("invalid/file"));
  }

  
  @Test
  public void testSavePrintsStackTraceOnIOException() throws IOException {
    State.initialize(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), null, null);
    State state = State.getInstance();
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
  public void test_load_non_existent_file() throws IOException{
    State.initialize(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), null, null);

    State state = State.getInstance();

    assertThrows(FileNotFoundException.class, () ->state.load("non_existent_file"));
  }

  @Test
  public void test_getInstance_throw(){
    assertThrows(IllegalStateException.class, () -> State.getInstance());
  }
    

  @Test
  public void test_getBuildings(){
    ArrayList<Building> buildings = new ArrayList<>();
    Building mine = new Mine("G", new BuildingType("Gold", Collections.emptyMap()), new ArrayList<>(), null, null);
    buildings.add(mine);
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(mine);
    Building factory = new Factory("GC", new BuildingType("GoldChain", Collections.emptyMap()), sources, null, null);
    buildings.add(factory);

    Map<String, Recipe> recipes = new HashMap<>();
    Map<String, Integer> ingredients = new HashMap<>();
    ingredients.put("Egg", 2);
    Recipe eggroll = new Recipe(3, ingredients, "EggRoll");
    recipes.put("EggRoll", eggroll);

    ArrayList<Recipe> stateRecipes = new ArrayList<>();
    stateRecipes.add(eggroll);
    
    ArrayList<BuildingType> types = new ArrayList<>();
    types.add(new BuildingType("EggRoll", recipes));

    RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
//    requestBroadcaster.addRecipes(eggroll);
//    requestBroadcaster.addBuildings(mine);
//    requestBroadcaster.addBuildings(factory);

    LogicTime logicTime = LogicTime.getInstance();
//    logicTime.addObservers(mine);
//    logicTime.addObservers(factory);
    
    State.initialize(buildings, types, stateRecipes, requestBroadcaster, logicTime);

    State state = State.getInstance();

    assertEquals(state.getBuildings(), buildings);
    assertEquals(factory, state.getBuildings("GC"));
    assertNull(state.getBuildings("DNE"));
  }
}
