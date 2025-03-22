package productsimulation.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;


public class FactoryType {
  private final String name;
  private Map<String, Recipe> recipes;

  public FactoryType(String name, Map<String, Recipe> recipes){
    this.name = name;
    this.recipes = recipes;
  }

  public Recipe getRecipeByProductName(String productName) {
    return recipes.get(productName);
  }
  
  @Override
  public String toString(){
    return "Factory Type\n{name='" + name + 
           "',\n recipes=" + printRecipes() + 
           "\n}";
  }

  
  //A helper function that returns a string representation of the recipes
  private String printRecipes() {
    StringBuilder result = new StringBuilder("[");
    
    if (recipes != null && !recipes.isEmpty()) {
        Iterator<Map.Entry<String, Recipe>> iterator = recipes.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Recipe r = iterator.next().getValue();
            result.append(r.getOutput());

            if (iterator.hasNext()) {
                result.append(", ");
            }
        }
    }

    result.append("]");
    return result.toString();
  }
  public String getName(){
    return name;
  }
}
