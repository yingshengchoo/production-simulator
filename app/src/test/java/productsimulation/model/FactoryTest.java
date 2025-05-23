package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productsimulation.Board;
import productsimulation.Coordinate;

public class FactoryTest {
  @BeforeEach
  public void cleanUpBefore() {
    Board.getBoard().cleanup();
  }

  @Test
  public void test_addFactory() {
    Factory.addFactory("factory1", new ArrayList<>(), null, null,
            new Coordinate(0, 0), new BuildingType("typename", new HashMap<>(), new Cost()));
    assertThrows(RuntimeException.class, ()->Factory.addFactory("factory2", new ArrayList<>(), null, null,
            new Coordinate(0, 0), new BuildingType("typename", new HashMap<>(), new Cost())));
  }

  @Test
  public void test_toString(){
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(new Factory("DoorInc", new BuildingType("Door", Collections.emptyMap()), Collections.emptyList(), null, null));
    sources.add(new Mine("DiamondMine", new BuildingType("Diamond", Collections.emptyMap()), Collections.emptyList(), null, null));

    Factory f = new Factory("PaperInc", new BuildingType("PaperFactory", Collections.emptyMap()), sources, null, null);
    String expected = "Factory\n{name='PaperInc',\n type='PaperFactory',\n sources=[DoorInc, DiamondMine],\n storage=[],\n request queue size=0\n}";
    assertEquals(expected, f.toString());

    Factory f2 = new Factory("PaperInc", new BuildingType("PaperFactory", Collections.emptyMap()), null, null, null);
    String expected2 = "Factory\n{name='PaperInc',\n type='PaperFactory',\n sources=[],\n storage=[],\n request queue size=0\n}";
    assertEquals(expected2, f2.toString());

    Factory f3 = new Factory("PaperInc", new BuildingType("PaperFactory", Collections.emptyMap()), new ArrayList<>(), null, null);
    String expected3 = "Factory\n{name='PaperInc',\n type='PaperFactory',\n sources=[],\n storage=[],\n request queue size=0\n}";
    assertEquals(expected3, f3.toString());

    assertEquals("PaperInc", f.getName());
  }

}
