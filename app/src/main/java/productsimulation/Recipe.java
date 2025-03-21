package productsimulation;

import java.util.Collections;
import java.io.Serializable;
import java.util.Map;

public class Recipe implements Serializable{
  private String name;
  private Map<String, Integer> ingredients;
  private final int latency;

  public Recipe(String name, int latency, Map<String, Integer> ingredients){
    this.name = name;
    this.latency = latency;
    this.ingredients = ingredients;
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

  @Override
  public String toString(){
        return "Recipe\n{name='" + name + 
           "',\n ingredients=" + ingredients.toString() + 
           ",\n latency=" + latency + 
           "\n}";
  }

  public String getName(){
    return name;
  }
  
}
