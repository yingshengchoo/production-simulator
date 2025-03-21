package productsimulation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class FactoryTypeTest {
  @Test
  public void test_getName() {
    FactoryType t = new FactoryType("type1", new ArrayList<>());
    assertEquals("type1", t.getName());
  }

  @Test
  public void test_toString(){
    FactoryType t = new FactoryType("type1", new ArrayList<>());
    String expected = "Factory Type\n{name='type1',\n sources=[]\n}";
    assertEquals(expected, t.toString());
  }
}
