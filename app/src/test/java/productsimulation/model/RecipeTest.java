package productsimulation.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;

public class RecipeTest {
  @Test
  public void test_getters() {
    Recipe r = new Recipe(1, Collections.emptyMap(), "hotpot"); 
    assertEquals("hotpot", r.getOutput());
    assertEquals(1, r.getLatency());
    assertEquals(Collections.emptyMap(),r.getIngredients());
  }

  @Test
  public void test_toString(){
    Recipe r = new Recipe(1, Collections.emptyMap(), "hotpot"); 
    String expected = "Recipe\n{output='hotpot',\n ingredients={},\n latency=1\n}";
    assertEquals(expected, r.toString());
  }
}


