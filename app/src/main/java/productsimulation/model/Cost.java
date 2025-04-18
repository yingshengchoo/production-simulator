package productsimulation.model;

import java.util.Map;
import java.util.HashMap;

public class Cost {
  private Map<String, Integer> cost;

  /**
   * Constructs a Cost object with a map of of the cost to making the factory type
   *
   * @param cost      is the cost to make the building
   */
  public Cost(Map<String, Integer> cost){
    this.cost = cost;
  }

  public Cost(){
    this(new HashMap<>());
  }
  
  public int getItemCost(String item){
    return cost.getOrDefault(item, 0);
  }

  public Map<String, Integer> getCostMap(){
    return  cost;
  }
  
}
