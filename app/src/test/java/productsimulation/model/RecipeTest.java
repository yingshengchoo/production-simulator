package productsimulation.model;


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
    ArrayList<Recipe> recipes = new ArrayList<>();
    recipes.add(new Recipe("out1", 3, Collections.emptyMap()));
    recipes.add(new Recipe("out2", 2, Collections.emptyMap()));
    FactoryType t = new FactoryType("type1", recipes);
    String expected = "Factory Type\n{name='type1',\n recipes=[out1, out2]\n}";
    assertEquals(expected, t.toString());

    FactoryType t2 = new FactoryType("type2", Collections.emptyList());
    String expected2 = "Factory Type\n{name='type2',\n recipes=[]\n}";
    assertEquals(expected2, t2.toString());
  }
}


