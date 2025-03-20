package productsimulation.model;

import java.util.List;

public class FactoryType {
  private final String name;
  private List<Recipe> recipes;

  public FactoryType(String name, List<Recipe> recipes){
    this.name = name;
    this.recipes = recipes;
  }
}
