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

  @Test
  public void test_set_and_getRecipeList(){
    Recipe r1 = new Recipe(12, Collections.emptyMap(), "recipe1").register();
    Recipe r2 = new Recipe(7, Collections.emptyMap(), "recipe2").register();
    Recipe r3 = new Recipe(2, Collections.emptyMap(), "recipe3").register();
    Recipe r4 = new Recipe(1, Collections.emptyMap()).register();
    assertEquals(r1, Recipe.getRecipe("recipe1"));
    assertEquals(r4, Recipe.getRecipe("output_placeholder"));
    assertNull(Recipe.getRecipe("Not in Recipe List"));
  }
}


