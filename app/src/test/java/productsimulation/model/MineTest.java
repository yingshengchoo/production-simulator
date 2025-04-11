package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class MineTest {
  @Test
  public void test_toString(){
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(new Factory("DoorInc", new BuildingType("Door", Collections.emptyMap()), Collections.emptyList(), null, null));
    sources.add(new Mine("DiamondMine", new BuildingType("Diamond", Collections.emptyMap()), Collections.emptyList(), null, null));
    Mine mine = new Mine("GoldMine", new BuildingType("Gold", Collections.emptyMap()), sources, null, null);
    
    String expected = "Mine\n{name='GoldMine',\n mine='Gold',\n sources=[DoorInc, DiamondMine],\n storage=[],\n request queue size=0\n}";
    assertEquals(expected, mine.toString());

    Mine mine2 = new Mine("GoldMine", new BuildingType("Gold", Collections.emptyMap()), null, null, null);
    
    String expected2 = "Mine\n{name='GoldMine',\n mine='Gold',\n sources=[],\n storage=[],\n request queue size=0\n}";
    assertEquals(expected2, mine2.toString());

    Mine mine3 = new Mine("GoldMine", new BuildingType("Gold", Collections.emptyMap()), new ArrayList<>(), null, null);
    
    String expected3 = "Mine\n{name='GoldMine',\n mine='Gold',\n sources=[],\n storage=[],\n request queue size=0\n}";
    assertEquals(expected3, mine3.toString());
  }

  @Test
  public void test_getname(){
    Mine mine = new Mine("GoldMine", new BuildingType("Gold", Collections.emptyMap()), Collections.emptyList(), null, null);
    assertEquals("GoldMine", mine.getName());
  }
}
