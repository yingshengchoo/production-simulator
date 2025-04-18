package productsimulation.model;


import java.util.Map;
import java.util.HashMap;

public class GlobalStorage {

  public static Map<String, Integer> globalStorageMap = new HashMap<>();

  /**
   * Adds the item to globalStorage.
   *
   * @param item    is the String of the item name to add to the globalStorage
   */
  public static void addItemToStorage(String item){
    globalStorageMap.put(item, getItemCount(item) + 1);
  }

  /**
   * get the Item count of the given item in the Global Storage
   *
   * @param item   is the Item to check
   */
  public static int getItemCount(String item){
    return globalStorageMap.getOrDefault(item, 0);
  }

  /**
   * Uses given number of resources of the given item from GlobalStorage. Returns the count of the remaining item needed
   * after using resources from the GlobalStorage. 
   *
   * @param item   is the Item needed
   * @param count  is the quantity needed from Storage
   * @return       is an int that returns the quantity still needed after
   */
  public static int useStorageItem(String item, int count){
    int updatedAmount = getItemCount(item) - count;
    //Not enough resources.
    if(updatedAmount < 0){
      globalStorageMap.put(item, 0);
      return -1 * updatedAmount;
    } else {  //enough resources.
      globalStorageMap.put(item, updatedAmount);
      return 0;
    }
  }

  /**
   * Returns the string representation of the Global Storage
   *
   * @return retursn the string format of the GlobalStorage
   */
  public static String globalStorageToString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Global Storage:\n");
    for (Map.Entry<String, Integer> entry : globalStorageMap.entrySet()) {
        sb.append("- ").append(entry.getKey())
          .append(": ").append(entry.getValue())
          .append("\n");
    }
    return sb.toString();
  }
}
