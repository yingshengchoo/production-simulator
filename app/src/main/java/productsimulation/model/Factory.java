package productsimulation.model;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.request.Request;
import productsimulation.request.RequestStatus;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import java.io.Serializable;
import java.util.List;

public class Factory extends Building implements Serializable {
  
  /**
   * Constructs a Factory with the specified name, type, sources, and policies.
   *
   * @param name         is the coordinate of the top left of the ship.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Factory(String name, FactoryType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy){
    super(name, type, sources, sourcePolicy, servePolicy);
  }

    public Factory(String name, FactoryType type, SourcePolicy sourcePolicy, ServePolicy servePolicy){
        super(name, type, sourcePolicy, servePolicy);
    }

    protected boolean goOneStep() {
        if(currentRequest == null) {
            if(!requestQueue.isEmpty()) {
//                [recipe selection]: Hw2 has fifo on cycle 8
                Log.level2Log("[request selection]: " + name + " has " + servePolicy.getName()
                + " on cycle " + LogicTime.getInstance().getStep());
                Request request = servePolicy.getRequest(requestQueue);
                Log.level2Log("    request:[" + name + ":" + request.getIngredient() + ":"
                        + request.getRequesterName() + "] is chosen");
                // 库存中request原料齐备才可以开工
                request.updateStatus(name, newIngredientsArrived, storage);
                newIngredientsArrived = false;
                if(request.getStatus() == RequestStatus.READY) {
                    request.readyToWorking(storage);
                } else {
                    Log.debugLog(name + " is waiting for ingredients");
                    return false;
                }
                currentRequest = request;
                Recipe currentRecipe = type.getRecipeByProductName(currentRequest.getIngredient());
                currentRemainTime = currentRecipe.getLatency();
            } else {
//                Log.debugLog("no request here: " + name);
                return true;
            }
        }

        // 实际工作用remainTime - 1模拟
        Log.debugLog(name + " is processing request: " +
                currentRequest.getIngredient() + ", " + currentRemainTime);
        currentRemainTime -= 1;
        totalRemainTime -= 1;
        return false;
    }


  //A helper function that returns a string representation of the sources.
  private String printSources() {
    StringBuilder result = new StringBuilder("[");
    if (super.sources != null && !super.sources.isEmpty()) {
        for (int i = 0; i < super.sources.size(); i++) {
            Building b = super.sources.get(i);
            result.append(b.getName());
           
            if (i < super.sources.size() - 1) {
                result.append(", ");
            }
        }
    }
    
    result.append("]");  
    return result.toString();
  }

  // Returns the String representation of Factory.
  @Override
  public String toString() {
    return "Factory\n{name='" + super.name + 
           "',\n type='" + super.type.getName() + 
           "',\n sources=" + printSources() + 
           // ",\n sourcePolicy=" + super.sourcePolicy.toString() + 
           // "',\n servePolicy=" + super.servePolicy.toString() + "'" +
           printStorageAndRequest() +
           "\n}";
  }
}

