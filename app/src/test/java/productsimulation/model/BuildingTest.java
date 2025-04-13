package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.SoleSourcePolicy;
import productsimulation.request.servePolicy.*;
import productsimulation.request.sourcePolicy.*;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;


public class BuildingTest {
  @BeforeEach
  public void cleanUpBefore() {
    Board.getBoard().cleanup();
    Building.buildingGlobalList.clear();
  }

  @Test
  public void test_getters() {
    ArrayList<Building> sources = new ArrayList<>();
    Factory source1 = new Factory("factory1", new BuildingType("type1", Collections.emptyMap()), Collections.emptyList(), null, null);
    Mine source2 = new Mine("mine1", new BuildingType("type2", Collections.emptyMap()), Collections.emptyList(), null, null);
    sources.add(source1);
    sources.add(source2);
    Building f = new Factory("DoorInc", new BuildingType("Door", Collections.emptyMap()), sources, new SourceQLen(), new FIFOPolicy());
    assertEquals("fifo", f.getServePolicy().getName());
    assertEquals("qlen", f.getSourcePolicy().getName());
    assertEquals("DoorInc", f.getName());
    assertEquals(sources, f.getSources());
    assertEquals(0, f.getTotalRemainTime());
    assertEquals(0, f.getCurrentRemainTime());
    assertEquals(null, f.getCurrentRequest());
    assertEquals(new HashMap<String, Integer>(), f.getStorage());

    f.changePolicy(new SoleSourcePolicy());
    assertEquals("sole", f.getSourcePolicy().getName());
    f.changePolicy(new SjfPolicy());
    assertEquals("sjf", f.getServePolicy().getName());
    
  }


  @Test
  public void test_equals_and_hash(){
    BuildingType t1 = new BuildingType("type1", Collections.emptyMap());
    Factory f1 =  new Factory("factory1", t1, Collections.emptyList(), null, null);
    Factory f2 = new Factory("factory1", t1, Collections.emptyList(), null, null);
    assertTrue(f1.equals(f2));
    Factory f3 = new Factory("factory2", new BuildingType("type1", Collections.emptyMap()), Collections.emptyList(), null, null);
    assertFalse(f1.equals(f3));

    assertTrue(f1.hashCode() == f2.hashCode());
    assertFalse(f1.hashCode() == f3.hashCode());

    assertFalse(f1.equals(null));
    assertFalse(f1.equals(new Recipe(1, Collections.emptyMap())));
  }
  
  @Test
  public void test_printStorage(){
    Building f = new Factory("DoorInc", new BuildingType("Door", Collections.emptyMap()), Collections.emptyList(), null, null);
    f.updateStorage("item1");
    f.updateStorage("item2");
    f.updateStorage("item2");
    String expected = " storage=[item2: 2, item1: 1],\n request queue size=0";
    assertEquals(expected, f.printStorageAndRequest());
  }

  @Test
  public void test_buildingCoordinates(){
    BuildingType t1 = new BuildingType("type1", Collections.emptyMap());
    Factory f1 =  new Factory("factory1", t1, Collections.emptyList(), null, null, new Coordinate(10,11));
    assertEquals(10,f1.getX());
    assertEquals(11,f1.getY());
  }
}
