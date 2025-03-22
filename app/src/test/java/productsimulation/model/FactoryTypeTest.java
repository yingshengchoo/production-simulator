package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import productsimulation.model.*;
import org.junit.jupiter.api.Test;

public class FactoryTypeTest {
  @Test
  public void test_getName() {
    FactoryType t = new FactoryType("type1", Collections.emptyMap());
    assertEquals("type1", t.getName());
  }

  @Test
  public void test_toString(){
    Map<String, Recipe> recipes = new LinkedHashMap<>();
    recipes.put("out1", new Recipe(3, Collections.emptyMap(), "out1"));
    recipes.put("out2", new Recipe(2, Collections.emptyMap(), "out2"));
    FactoryType t = new FactoryType("type1", recipes);
    String expected = "Factory Type\n{name='type1',\n recipes=[out1, out2]\n}";
    assertEquals(expected, t.toString());

    FactoryType t2 = new FactoryType("type2", Collections.emptyMap());
    String expected2 = "Factory Type\n{name='type2',\n recipes=[]\n}";
    assertEquals(expected2, t2.toString());
  }
}
