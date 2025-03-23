package productsimulation.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
public class Recipe implements Serializable{
  private String output;
  // ingredients应当为LinkedHashMap，或者其它能保留顺序的map。ingredients顺序会影响request传播顺序。
  private Map<String, Integer> ingredients;
  private final int latency;

  public static Recipe getRecipe(String item, List<Recipe> recipeList) {
    for (Recipe recipe : recipeList) {
      if (recipe.output.equals(item)) {
        return recipe;
      }
    }
    return null;
  }

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

  @Override
  public String toString(){
        return "Recipe\n{output='" + output + 
           "',\n ingredients=" + ingredients.toString() + 
           ",\n latency=" + latency + 
           "\n}";
  }
  
}
