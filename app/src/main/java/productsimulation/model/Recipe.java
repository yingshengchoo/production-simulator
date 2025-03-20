package productsimulation.model;

import java.util.Collections;
import java.util.Map;

public class Recipe {
  private String output;
  private Map<String, Integer> ingredients;
  private final int latency;

  public Recipe(int latency, Map<String, Integer> ingredients) {
    this(latency, ingredients, "output_placeholder");
  }

  public Recipe(int latency, Map<String, Integer> ingredients, String output){
    this.latency = latency;
    this.ingredients = ingredients;
    this.output = output;
  }

  /**
   * Retrieves an unmodifiable view of the ingredients required for the recipe.
   *
   * @return a map of ingredient names to their required quantities, as an unmodifiable map
   */
  public Map<String, Integer> getIngredients() {
    return Collections.unmodifiableMap(ingredients);
  }

  public int getLatency() {
    return latency;
  }

  public String getOutput() { return output; }
}
