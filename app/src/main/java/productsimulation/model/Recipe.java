package productsimulation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
public class Recipe implements Serializable{

  public static List<Recipe> recipeGlobalList = new ArrayList<>();
  private String output;
  // ingredients应当为LinkedHashMap，或者其它能保留顺序的map。ingredients顺序会影响request传播顺序。
  private Map<String, Integer> ingredients;
  private final int latency;

  public String getWaste() {
    return waste;
  }

  public void setWaste(String waste) {
    this.waste = waste;
  }

  private String waste;

  
  public static Recipe getRecipe(String item) {
    for (Recipe recipe : recipeGlobalList) {
      if (recipe.output.equals(item)) {
        return recipe;
      }
    }
    return null;
  }

    /**
     * Constructs a Recipe with a given latency and ingredeints.
     *
     * @param latency             is the time it takes to complete the recipe.
     * @param ingredients         is the map of ingredients needed to make the recipe.
     */
//    only for some unit test
  public Recipe(int latency, Map<String, Integer> ingredients) {
    this(latency, ingredients, "output_placeholder");
  }

  
    /**
     * Constructs a Recipe with a given latency, ingredeints, and output name.
     *
     * @param latency             is the time it takes to complete the recipe.
     * @param ingredients         is the map of ingredients needed to make the recipe.
     * @param output              is the name of the output of the recipe.
     */
  public Recipe(int latency, Map<String, Integer> ingredients, String output){
    this.latency = latency;
    this.ingredients = ingredients;
    this.output = output;
  }

  public Recipe register() {
    recipeGlobalList.add(this);
    return this;
  }

  /**
   * Retrieves an unmodifiable view of the ingredients required for the recipe.
   *
   * @return a map of ingredient names to their required quantities, as an unmodifiable map
   */
  public Map<String, Integer> getIngredients() {
    return Collections.unmodifiableMap(ingredients);
  }

  
  /**
   * Returns the latency of the recipe.
   *
   * @return  the int value of the latency.
   */
  public int getLatency() {
    return latency;
  }

  
  /**
   * Returns the output name of the recipe.
   *
   * @return  the String name of the output.
   */
  public String getOutput() { return output; }

   
  /**
   * Returns the String representation of the Recipe
   *
   * @return  the String representation..
   */
  @Override
  public String toString(){
        return "Recipe\n{output='" + output + 
           "',\n ingredients=" + ingredients.toString() + 
           ",\n latency=" + latency + 
           "\n}";
  }
  
}
