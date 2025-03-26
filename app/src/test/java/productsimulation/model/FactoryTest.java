package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import productsimulation.model.Building;
import java.util.Collections;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class FactoryTest {
  @Test
  @Disabled("waiting for debug")
  public void test_toString(){
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(new Factory("DoorInc", new FactoryType("Door", Collections.emptyMap()), Collections.emptyList(), null, null));
    sources.add(new Mine("DiamondMine", new FactoryType("Diamond", Collections.emptyMap()), Collections.emptyList(), null, null));

    Factory f = new Factory("PaperInc", new FactoryType("PaperFactory", Collections.emptyMap()), sources, null, null);
    String expected = "Factory\n{name='PaperInc',\n type='PaperFactory',\n sources=[DoorInc, DiamondMine]\n}";
    assertEquals(expected, f.toString());

    Factory f2 = new Factory("PaperInc", new FactoryType("PaperFactory", Collections.emptyMap()), null, null, null);
    String expected2 = "Factory\n{name='PaperInc',\n type='PaperFactory',\n sources=[]\n}";
    assertEquals(expected2, f2.toString());

    Factory f3 = new Factory("PaperInc", new FactoryType("PaperFactory", Collections.emptyMap()), new ArrayList<>(), null, null);
    String expected3 = "Factory\n{name='PaperInc',\n type='PaperFactory',\n sources=[]\n}";
    assertEquals(expected3, f3.toString());

    assertEquals("PaperInc", f.getName());
  }

}
