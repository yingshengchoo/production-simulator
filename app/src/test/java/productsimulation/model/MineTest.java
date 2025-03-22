package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class MineTest {
  @Test
  public void test_toString(){
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(new Factory("DoorInc", new FactoryType("Door", new ArrayList<>()), Collections.emptyList(), null, null));
    sources.add(new Mine("DiamondMine", "Diamond", Collections.emptyList(), null, null));
    Mine mine = new Mine("GoldMine", "Gold", sources, null, null);
    
    String expected = "Mine\n{name='GoldMine',\n mine='Gold',\n sources=[DoorInc, DiamondMine]\n}";
    assertEquals(expected, mine.toString());
  }

  @Test
  public void test_getname(){
    Mine mine = new Mine("GoldMine", "Gold", Collections.emptyList(), null, null);
    assertEquals("GoldMine", mine.getName());
  }
}
