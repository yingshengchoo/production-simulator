package productionsimulation;

import java.util.Map;

public class Recipe {
  private String output;
  private Map<String, Integer> ingredients;
  private final int latency;

  public Recipe(int latency){
    this.latency = latency;
  }
}
