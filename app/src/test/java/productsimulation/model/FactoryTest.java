package productsimulation.model.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class FactoryTest {
  @Test
  public void test_toString(){
    ArrayList<Building> sources = new ArrayList<>();
    sources.add(new Factory("DoorInc", new FactoryType("Door", new ArrayList<>()), Collections.emptyList(), null, null));
    sources.add(new Mine("DiamondMine", new FactoryType("Diamond", new ArrayList<>()), Collections.emptyList(), null, null));

    Factory f = new Factory("PaperInc", new FactoryType("PaperFactory", new ArrayList<>()), sources, null, null);
    String expected = "Factory\n{name='PaperInc',\n type='PaperFactory',\n sources=[DoorInc, DiamondMine]\n}";
    assertEquals(expected, f.toString());

    assertEquals("PaperInc", f.getName());
  }

}
