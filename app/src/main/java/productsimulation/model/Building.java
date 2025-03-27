package productsimulation.model;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.request.Policy;
import productsimulation.request.Request;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.io.Serializable;

import java.util.*;

public abstract class Building implements Serializable {

    private static List<Building> buildings = new ArrayList<>();

    public static List<Building> getBuildings() {
        return buildings;
    }

    protected final String name;
    protected FactoryType type;
    protected Request currentRequest;
    protected int currentRemainTime;
    protected int totalRemainTime = 0;
    protected List<Request> requestQueue;
    protected Map<String, Integer> storage;
    // 如果没有新增原料，就不输出库存检查过程。不论为true false，库存检查本身都会进行。
    boolean newIngredientsArrived = false;
    protected List<Building> sources;
    protected SourcePolicy sourcePolicy;
    protected ServePolicy servePolicy;

    /**
     * Constructs a Building with the specified name, type, sources, and policies.
     *
     * @param name         is the coordinate of the top left of the ship.
     * @param type         is the Building Type.
     * @param sources      is list of buildings that provides the ingredients to make the recipes.
     * @param sourcePolicy is the policy that the building uses to select between sources.
     * @param servePolicy  is the policy that the building uses to select between requests.
     */
    public Building(String name, FactoryType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy) {
        this.name = name;
        this.type = type;
        this.sources = sources;
        this.sourcePolicy = sourcePolicy;
        this.servePolicy = servePolicy;

        requestQueue = new ArrayList<>();
        storage = new HashMap<>();
    }

    public Building(String name, FactoryType type, SourcePolicy sourcePolicy, ServePolicy servePolicy) {
        this.name = name;
        this.type = type;
        this.sourcePolicy = sourcePolicy;
        this.servePolicy = servePolicy;
        requestQueue = new ArrayList<>();
        storage = new HashMap<>();
    }

    // 按recipe顺序dfs传播request
    public void addRequest(Request request) {
//        [ingredient assignment]: wood assigned to W to deliver to D
        Log.level1Log("[ingredient assignment]: " + request.getIngredient() + " assigned to " + name +
                " to deliver to " + request.getRequesterName());
//        [source selection]: D (qlen) has request for door on 0
        Log.level2Log("[source selection]: " + name + " (" + sourcePolicy.getName() + ") has request for "
                + request.getIngredient() + " on " + LogicTime.getInstance().getStep());
        requestQueue.add(request);

        // 更新total time
        totalRemainTime += request.getLatency();

        Recipe parentRecipe = type.getRecipeByProductName(request.getIngredient());
        Map<String, Integer> ingredients = parentRecipe.getIngredients();
        for(String ingredient: ingredients.keySet()) {
            int num = ingredients.get(ingredient);
            for(int i = 0; i < num; i++) {
//                [D:door:0] For ingredient wood
                Log.level2Log("[" + name + ":" + request.getIngredient() + ":" + LogicTime.getInstance().getStep()
                + "] For ingredient " + ingredient);
                Building chosenSource = sourcePolicy.getSource(sources, ingredient);
                Log.level2Log("    selecting " + chosenSource.getName());
                Recipe childRecipe = chosenSource.type.getRecipeByProductName(ingredient);
                Request req = new Request(ingredient, childRecipe,this);
                chosenSource.addRequest(req);
            }
        }
    }

    public boolean canProduce(String itemName) {
        return type.getRecipeByProductName(itemName) != null;
    }

    public void setSources(List<Building> sources) {
        this.sources = sources;
    }

  private String printStorage() {
    StringBuilder result = new StringBuilder(" storage=[");
    
    if (storage != null && !storage.isEmpty()) {
        Iterator<Map.Entry<String, Integer>> iterator = storage.entrySet().iterator();
        
        while (iterator.hasNext()) {
          Map.Entry<String, Integer> entry = iterator.next();
          String item = entry.getKey();
            int count = entry.getValue();
            result.append(item + ": " + count);

            if (iterator.hasNext()) {
                result.append(", ");
            }
        }
    }

    result.append("]");
    return result.toString();
   }

    private String printRequestQueue() {

    StringBuilder result = new StringBuilder(" request queue size=");
    result.append(requestQueue.size());
    
    //StringBuilder result = new StringBuilder("[");
    // if (requestQueue != null && !requestQueue.isEmpty()) {
    //     for (int i = 0; i < requestQueue.size(); i++) {
    //         Request r = requestQueue.get(i);
    //         result.append(r.getName());
           
    //         if (i < requestQueue.size() - 1) {
    //             result.append(", ");
    //         }
    //     }
    // }
    
    //result.append("]");  
    return result.toString();
  }

  public String printStorageAndRequest(){
    return printStorage() + ",\n" + printRequestQueue();
  }
    public int getRequestCount() {
        return requestQueue.size();
    }

    // return: still have/no request for this building now.
    protected abstract boolean goOneStep();

    private void update() {
        if(currentRequest != null && currentRemainTime == 0) {
            //      [ingredient delivered]: hinge to D from Hw2 on cycle 11
            Log.level2Log("[ingredient delivered]: " + currentRequest.getIngredient()
                    + " to " + currentRequest.getRequesterName()
            + " from " + name + " on cycle " + LogicTime.getInstance().getStep());
            currentRequest.doneReportAndTransport();
            requestQueue.remove(currentRequest);
            currentRequest = null;
        }
    }

    public void updateStorage(String itemName) {
        storage.put(itemName, storage.getOrDefault(itemName, 0) + 1);
        newIngredientsArrived = true;
    }

    public void changeServePolicy(ServePolicy servePolicy) {
        this.servePolicy = servePolicy;
    }

    public void changeSourcePolicy(SourcePolicy sourcePolicy) {
        this.sourcePolicy = sourcePolicy;
    }

    public void changePolicy(Policy policy) {
        if (policy instanceof ServePolicy) {
            changeServePolicy((ServePolicy) policy);
        } else {
            changeSourcePolicy((SourcePolicy) policy);
        }
    }

    public boolean notified() {
        return goOneStep();
    }

    public void updateNotified() {
        update();
    }

    public int getTotalRemainTime() {
        return totalRemainTime;
    }

    public String getName() {
        return name;
    }

    public List<Building> getSources() {
        return sources;
    }

    public ServePolicy getServePolicy(){
      return servePolicy;
    }

    public SourcePolicy getSourcePolicy(){
      return sourcePolicy;
    }
  
    public int getCurrentRemainTime(){
        return currentRemainTime;
    }

    public Request getCurrentRequest(){
        return currentRequest;
    }

    public Map<String, Integer> getStorage(){
        return storage;
    }

    
  
   @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Building building = (Building) o;
        return Objects.equals(name, building.name) && Objects.equals(type, building.type);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }

    public List<Request> getRequestQueue() {
        return requestQueue;
    }


}
