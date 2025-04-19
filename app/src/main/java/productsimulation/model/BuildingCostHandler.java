package productsimulation.model;

import productsimulation.request.sourcePolicy.*;
import productsimulation.request.Request;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class BuildingCostHandler {

  public static List<Building> inConstructionBuildingList = new ArrayList<>();

  /**
   * Constructs the building. If the resources needed to construct Building is insufficient, reqeust them.
   * When resources are ready, create the building.
   *
   */
  public static void constructBuilding(Building b){
    BuildingType type = b.getBuildingType();
    Cost cost = type.getCost();
    if(cost.isFree()){
      b.register();
    } else {
      getResourceNeededAfterUsingGlobalStorage(b);
      inConstructionBuildingList.add(b);
    }
  }
  
  /**
   * Returns the Map of the Cost to build the BuildingType where cost is negative. 
   *
   * @param type     is the BuildingType of the building to constructx
   * @return         returns the map of the cost to create the Builidng
   */
  private static void getResourceNeededAfterUsingGlobalStorage(Building b){
    Cost cost = b.getBuildingType().getCost();
    Map<String, Integer> costMap = cost.getCostMap();
    for(Map.Entry<String, Integer> entry : costMap.entrySet()){
      String item = entry.getKey();
      int requiredAmount = entry.getValue();
      int missingAmount = GlobalStorage.useStorageItem(item, requiredAmount); //negative number if missing resouce, otherwise 0;
      sendRequestForBuildingResource(item, missingAmount);
    }
  }

  /**
   * Sends requests to make the resources needed to construct the building.
   *
   * @param item              is the resources needed to construct the building
   * @param missingAmount     is the count of the item still needed to construct building.
   */
  private static void sendRequestForBuildingResource(String item, int missingAmount){
    for(int i = 0; i < missingAmount; i ++){
      SourcePolicy sourcePolicy = new SourceQLen();
      Building chosenSource = sourcePolicy.getSource(Building.buildingGlobalList, item);
      Recipe recipe = Recipe.getRecipe(item);
      Request request = new Request(item, recipe, null);
      chosenSource.addRequest(request);
    }
  }

  
  
  private static void supplyItemToBuildingInConstruction(String item, int count){
    for(Building b : inConstructionBuildingList){
      int value = b.getStorageItem(item);
      if(value >= 0){ 
        continue;
      }
      value = -1 * value; //now positive
      if(value >= count){
        b.updateConstruction(item, count); 
        GlobalStorage.useStorageItem(item, count);
        return;
      } else {
        b.updateConstruction(item, value);
        GlobalStorage.useStorageItem(item, value);
        count -= value;
      }
    }
  }
  
  /**
   * Supplies 
   */
  public static void update(){
    Map<String, Integer> storage = GlobalStorage.getGlobalStorageMap();
    for(Map.Entry<String, Integer> entry: storage.entrySet()){
      supplyItemToBuildingInConstruction(entry.getKey(), entry.getValue());
    }
  }
  
}
