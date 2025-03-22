package productsimulation.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
}
