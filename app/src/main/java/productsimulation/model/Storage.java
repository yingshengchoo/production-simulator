package productsimulation.model;

import productsimulation.Coordinate;
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
import java.util.HashMap;

public class Storage extends Building {

  private double priority;
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
  public Storage(String name, String itemToStore, List<Building> sources, int totalCapacity, double priority, SourcePolicy sourcePolicy, ServePolicy servePolicy, Coordinate coordinate){
    super(name, new FactoryType(name, Map.of(itemToStore, new Recipe(Recipe.getRecipe(itemToStore).getLatency(), new HashMap<>(), itemToStore))), sources, sourcePolicy, new FIFOPolicy(), coordinate); // Storage only supports FIFO!
    this.recipe = new Recipe(Recipe.getRecipe(itemToStore).getLatency(), new HashMap<>(), itemToStore);
    this.totalCapacity = totalCapacity;
    this.priority = priority;
    this.frequency = -1;
    this.R = totalCapacity;
    super.storage.put(itemToStore, 0);
    readyQueue = new ArrayList<>();
  }

  // Constructor without coordinate input
  public Storage(String name, String itemToStore, List<Building> sources, int totalCapacity, double priority, SourcePolicy sourcePolicy, ServePolicy servePolicy){
    super(name, new FactoryType(name, Map.of(itemToStore, new Recipe(Recipe.getRecipe(itemToStore).getLatency(), new HashMap<>(), itemToStore))), sources, sourcePolicy, new FIFOPolicy()); // Storage only supports FIFO!
    this.recipe = new Recipe(Recipe.getRecipe(itemToStore).getLatency(), new HashMap<>(), itemToStore);
    this.totalCapacity = totalCapacity;
    this.priority = priority;
    this.frequency = -1;
    this.R = totalCapacity;
    super.storage.put(itemToStore, 0);
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
  public Storage(String name, String itemToStore, int totalCapacity, double priority, SourcePolicy sourcePolicy, ServePolicy servePolicy, Coordinate coordinate){
    this(name, itemToStore, new ArrayList<>(), totalCapacity, priority, sourcePolicy, servePolicy, coordinate);
  }

  @Override
  public boolean goOneStep() {
    //Serve policy used should always be fifo.

    //Has stock and has request to handle
    while(getReqCount() > 0){
      //Serve Policy is always Fifo
      Log.level2Log("[request selection]: " + name + " has serve policy 'fifo' on cycle " + LogicTime.getInstance().getStep());
      if(!hasStock()){
        Log.debugLog(name + " is waiting for ingredients");
        return false;
      }
      //FIFO POLICY HERE: Directly writing it without calling the pre written funciton :P
      Request request = requestQueue.get(0);
      request.readyToWorking(storage); //consume the storages
      requestQueue.remove(request); 
      //Adds request to ready queue which will be sent back to requester the next time step.
      readyQueue.add(request);
      R--;//Update R 
      //keeps updating until we get a request
      currentRemainTime = recipe.getLatency();
    }
    return true;
  }

  @Override
  public int getCurrentRemainTime(){
    if(getStockCount() > 0){
      return 0;
    } else {
      return 1; //needs update here, not sure if it matches the logic for recursive lat.
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

  //checks that the item is in ready queue, for testing**
  public int getReadyQueueCount(){
    return readyQueue.size();
  }
  //check the request queue count, for testing
  public int getReqCount(){
    return requestQueue.size();
  }

  @Override
  public int getTotalRemainTime(){
    if(hasStock()){
      return -1 * recipe.getLatency() * getStockCount();
    } else {
      return recipe.getLatency() * requestQueue.size();
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
    return storage.getOrDefault(recipe.getOutput(), 0);
  }

  public int getR(){
    return R;
  }

  public int getTotalCapacity(){
    return totalCapacity;
  }

  public double getPriority(){
    return priority;
  }

  public String getRecipeOutput(){
    return recipe.getOutput();
  }
  
  @Override
  public void addRequest(Request request){
      //[ingredient assignment]: wood assigned to W to deliver to D
      Log.level1Log("[ingredient assignment]: " + request.getIngredient() + " assigned to " + name +
                    " to deliver to " + request.getRequesterName());
//    [source selection]: D (qlen) has request for door on 0
      Log.level2Log("[source selection]: " + name + " (" + sourcePolicy.getName() + ") has request for "
                   + request.getIngredient() + " on " + LogicTime.getInstance().getStep());

      //有貨不用往下椽
      if(getStockCount() > 0){
        readyQueue.add(request);
        R--;
      } else {
        //沒貨繼續往下傳
        requestQueue.add(request);
        //Storage only sends request to sources periodically.
        //so sends request to sources in goOneStep()
        Log.level2Log("[" + name + ":" + recipe.getOutput() + ":" + LogicTime.getInstance().getStep()
                + "] For Storage " + name);
        //only do this if storage == 0, otherwise return the request.
        Building chosenSource = sourcePolicy.getSource(sources, recipe.getOutput());
        Log.level2Log("    selecting " + chosenSource.getName());
        Recipe childRecipe = chosenSource.type.getRecipeByProductName(recipe.getOutput());
        Request req = new Request(recipe.getOutput(), childRecipe, this);
        chosenSource.addRequest(req);
      }
  }
  
  /**
   * Sends request to sources  
   */
  public void sendRequest(){
    if (getFrequency() == -1){
      return;
    }
    boolean isOnFrequency = (LogicTime.getInstance().getStep() % frequency == 0);
    if(isOnFrequency){
      Log.level2Log("[" + name + ":" + recipe.getOutput() + ":" + LogicTime.getInstance().getStep()
                + "] For Storage " + name);
      Building chosenSource = sourcePolicy.getSource(sources, recipe.getOutput());
      Log.level2Log("    selecting " + chosenSource.getName());
      Recipe childRecipe = chosenSource.type.getRecipeByProductName(recipe.getOutput());
      Request req = new Request(recipe.getOutput(), childRecipe, this);
      chosenSource.addRequest(req);
      R--;
    }
  }

  // returns the frequency of the Storage system.
  public int getFrequency(){
    if(R == 0){
      return -1;
    } else {
      return (int)Math.ceil((double)(totalCapacity * totalCapacity) / (double)(R * priority));
    }
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
