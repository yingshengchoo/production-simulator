package productsimulation.model;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
public class Cost implements Serializable {
  private Map<String, Integer> cost;

  /**
   * Constructs a Cost object with a map of of the cost to making the factory type
   *
   * @param cost      is the cost to make the building
   */
  public Cost(Map<String, Integer> cost){
    this.cost = cost;
  }

  /**
   * Constructs a Cost object with a building that costs nothing
   */
  public Cost(){
    this(new HashMap<>());
  }

  /**
   * checks if the building is free or not
   */
  public boolean isFree(){
    int totalSum = cost.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    if(totalSum > 0){
      return false;
    } else {
      return true;
    }
  }

  /**
   * returns the cost of the specific item.
   *
   * @param cost      is the cost to make the building
   */
  public int getItemCost(String item){
    return cost.getOrDefault(item, 0);
  }

  /**
   * Returns the map of the costs
   *
   * @param cost      is the cost to make the building
   */
  public Map<String, Integer> getCostMap(){
    return  cost;
  }
  
}
