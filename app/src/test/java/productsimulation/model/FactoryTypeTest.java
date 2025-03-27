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

  @Test
  public void test_equals_and_hash(){
    Map<String, Recipe> recipes = new HashMap<>();
    recipes.put("r1", new Recipe(1,Collections.emptyMap(), "r1"));
    recipes.put("r2", new Recipe(1,Collections.emptyMap(), "r2"));
    
    FactoryType t = new FactoryType("type1", recipes);

    assertTrue(t.equals(new FactoryType("type1", recipes)));
    assertFalse(t.equals(new FactoryType("type2", recipes)));
    assertFalse(t.equals(new FactoryType("type1", Collections.emptyMap())));
    assertFalse(t.equals(new Recipe(1, Collections.emptyMap())));
    assertFalse(t.equals(null));

    FactoryType t2 = new FactoryType("type1", recipes);
    FactoryType t3 = new FactoryType("type2", recipes);
    assertEquals(t.hashCode(), t2.hashCode());
    assertNotEquals(t.hashCode(), t3.hashCode());
  }
}
