package productsimulation;

import java.util.List;
import Serializable;

public class FactoryType implements Serializable {
  private final String name;
  private List<Recipe> recipes;

  public FactoryType(String name){
    this.name = name;
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
        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);
            result.append(r.getName());
           
            if (i < recipes.size() - 1) {
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
