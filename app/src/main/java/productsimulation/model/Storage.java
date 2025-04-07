package productsimulation.model;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.request.Request;
import productsimulation.request.RequestStatus;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Storage extends Building {

  private int priority;
  private int frequency;
  private int totalCapacity;
  private List<Request> readyQueue;
  private Recipe recipe;
  private int R;
  /**
   * Constructs a Mine with the specified name, type, sources, and policies.
   *
   * @param name         is the name of the Mine.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Storage(String name, Recipe recipe, List<Building> sources, int totalCapacity, int priority, SourcePolicy sourcePolicy, ServePolicy servePolicy){
    super(name, new FactoryType(name, Map.of(recipe.getOutput(), recipe)), sources, sourcePolicy, new FIFOPolicy()); // Storage only supports FIFO!
    this.recipe = recipe;
    this.totalCapacity = totalCapacity;
    this.priority = priority;
    this.frequency = -1;
    this.R = totalCapacity;
    readyQueue = new ArrayList<>();
  }

  /**
   * Constructs a Mine with the specified name, type, sources, and policies.
   *
   * @param name         is the name of the Mine.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Storage(String name, FactoryType type, Recipe storageType, int totalCapacity, int priority, SourcePolicy sourcePolicy, ServePolicy servePolicy){
        super(name, type, sourcePolicy, servePolicy);
        this.totalCapacity = totalCapacity;
        this.priority = priority;
        this.frequency = -1;
        this.R = totalCapacity;
  }

  /**
   * Updates the frequency of the request 
   */
  public void updateFrequency(){
    //frequency should never be 0.
    if(R == 0){
      this.frequency = -1;
    } else {
     this.frequency = (int)Math.ceil((totalCapacity * totalCapacity) / (R * priority));
    }
  }  

  @Override
  public boolean goOneStep() {
    //Serve policy used should always be fifo.
    
    if(!requestQueue.isEmpty()) {
      //    [recipe selection]: Hw2 has fifo on cycle 8
      Log.level2Log("[request selection]: " + name + " has serve policy '" + servePolicy.getName()
                  + "' on cycle " + LogicTime.getInstance().getStep());
      for(Request request: requestQueue) {
        // 库存中request原料齐备才可以开工
        request.updateStatus(name, newIngredientsArrived, storage);
        newIngredientsArrived = false;
      }

      Request request = servePolicy.getRequest(requestQueue);

      if (request == null) {
        Log.level2Log("    Request queue is not empty, but no request is is chosen in " + name);
        return false;
      }
      while(request != null){
        Log.level2Log("    request:[" + name + ":" + request.getIngredient() + ":"
                      + request.getRequesterName() + "] is chosen");
        if(request.getStatus().equals(RequestStatus.READY)) {
          request.readyToWorking(storage);
        } else {
          Log.debugLog(name + " is waiting for ingredients");
          return false;
        }
        //add request to request queue to send at next time step
        readyQueue.add(request);
        R--;//consumes one storage
        //keeps updating until we get a request
        currentRemainTime = recipe.getLatency();
        request = servePolicy.getRequest(requestQueue);
      }
    } else {
      //      Log.debugLog("no request here: " + name);
      return true;
    }

    if(requestQueue.isEmpty()){
      return false;
    } else{
      return true;
    }
  }

  @Override
  public int getRequestCount() {
    if(hasStock()){
      //if has stock there should be no request
      return -1 * getStockCount();
    } else {
      return requestQueue.size();
    }
  }

  @Override
  public int getTotalRemainTime(){
    if(hasStock()){
      return -1 * recipe.getLatency() * getStockCount();
    } else {
      return totalRemainTime;
    }
  }

  private boolean hasStock(){
    return getStockCount() != 0;
  }

  @Override
  public void updateStorage(String itemName){
    super.updateStorage(itemName);
    //No need here R--; I believe...
    //source increases storage (R++) and outstanding request -1 (R--) which cancels out
  }
  
  @Override
  protected void update(){
    for(Request request: readyQueue){
      Log.level2Log("[ingredient delivered]: " + request.getIngredient()
                    + " to " + request.getRequesterName()
            + " from " + name + " on cycle " + LogicTime.getInstance().getStep());
      request.doneReportAndTransport();
      currentRequest = null;//should always be null
    }
    
    readyQueue.clear();
  }

  public int getStockCount(){
    return storage.size();
  }

  public int getR(){
    return R;
  }
  
  @Override
  public void addRequest(Request request){
      //[ingredient assignment]: wood assigned to W to deliver to D
      Log.level1Log("[ingredient assignment]: " + request.getIngredient() + " assigned to " + name +
                    " to deliver to " + request.getRequesterName());
//    [source selection]: D (qlen) has request for door on 0
      Log.level2Log("[source selection]: " + name + " (" + sourcePolicy.getName() + ") has request for "
                   + request.getIngredient() + " on " + LogicTime.getInstance().getStep());
      requestQueue.add(request);

      // 更新total time
      totalRemainTime += request.getLatency();
      //Storage only sends request to sources periodically.
      //so sends request to sources in goOneStep()
      R++;
  }

  /**
   * Sends request to sources  
   */
  public void sendRequest(){
    updateFrequency();
    boolean validFrequency = (frequency != -1);
    boolean isOnFrequency = (LogicTime.getInstance().getStep() % frequency == 0);
    if(validFrequency && isOnFrequency){
      Log.level2Log("[" + name + ":" + recipe.getOutput() + ":" + LogicTime.getInstance().getStep()
                + "] For Storage " + name);
      Building chosenSource = sourcePolicy.getSource(sources, recipe.getOutput());
      Log.level2Log("    selecting " + chosenSource.getName());
      Recipe childRecipe = chosenSource.type.getRecipeByProductName(recipe.getOutput());
      Request req = new Request(recipe.getOutput(), childRecipe,this);
      chosenSource.addRequest(req);
      R--;
    }
  }
  
  public int getFrequency(){
    return frequency;
  }
  
  @Override
  public String toString() {
    return "Storage\n{name='" + super.name +   
           "',\n stores='" + recipe.getOutput() + 
           "',\n sources=" + printSources() + 
           ",\n capacity=" + totalCapacity +
      // ",\n sourcePolicy=" + super.sourcePolicy.toString() + 
           // "',\n servePolicy=" + super.servePolicy.toString() + "'" +
           ",\n" + printStorageAndRequest()+
           "\n}";
  }
}
