package productionsimulation;

import java.util.List;

public class FactoryType {
  private final String name;
  private List<Recipe> recipes;

  public FactoryType(String name){
    this.name = name;
  }
}
