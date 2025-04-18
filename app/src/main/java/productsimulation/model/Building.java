package productsimulation.model;

import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.model.road.Road;
import productsimulation.request.Policy;
import productsimulation.request.Request;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.io.Serializable;

import java.util.*;

public abstract class Building implements Serializable {

    protected final String name;
    protected BuildingType type;
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

    protected Coordinate coordinate;


    public static List<Building> buildingGlobalList = new ArrayList<>();
    public static Building getBuilding(String item) {
        for (Building b : buildingGlobalList) {
            if (b.getName().equals(item)) {
                return b;
            }
        }
        return null;
    }

    public Building(String name, BuildingType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy, Coordinate coordinate) {
        this.name = name;
        this.type = type;
        this.sourcePolicy = sourcePolicy;
        this.servePolicy = servePolicy;
        requestQueue = new ArrayList<>();
        storage = new HashMap<>();
        this.sources = sources;
        this.coordinate = coordinate;
    }

//    子类的实现中，至少要将实例加进全局列表，也可以做其它在确保状态合法之后才做的事情
//    返回值是building，是为了避免new完之后还要再额外加一行register()调用，仅用于减少代码行数
    public abstract Building register();

    /**
     * Constructs a Building with the specified name, type, sources, and policies.
     *
     * @param name         is the name the Building.
     * @param type         is the Building Type.
     * @param sources      is list of buildings that provides the ingredients to make the recipes.
     * @param sourcePolicy is the policy that the building uses to select between sources.
     * @param servePolicy  is the policy that the building uses to select between requests.
     */
    public Building(String name, BuildingType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy) {
        this(name, type, sourcePolicy, servePolicy);
        this.sources = sources;
    }

    public Building(String name, BuildingType type, SourcePolicy sourcePolicy, ServePolicy servePolicy) {
        this.name = name;
        this.type = type;
        this.sourcePolicy = sourcePolicy;
        this.servePolicy = servePolicy;
        requestQueue = new ArrayList<>();
        storage = new HashMap<>();
        this.coordinate = BuildingHandler.getValidCoordinate();
    }
  
    /**
     * Adds the request into the relavant buildings.
     *
     * @param request    Is the request to be processed.
     */
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
                Building chosenSource = sourcePolicy.getSource(sources, ingredient);
                //[D:door:0] For ingredient wood
                Log.level2Log("[" + name + ":" + request.getIngredient() + ":" + LogicTime.getInstance().getStep()
                        + "] For ingredient " + ingredient + " , transport latency=" + Road.getDistance(chosenSource, this));
                Log.level2Log("    selecting " + chosenSource.getName());
                Recipe childRecipe = chosenSource.type.getRecipeByProductName(ingredient);
                Request req = new Request(ingredient, childRecipe,this);

                // set transport latency!
                req.setTransLatency(Road.getDistance(chosenSource, this) + 1);

                chosenSource.addRequest(req);
            }
        }
    }

    /**
     * Checks whether a Recipe can be produced.
     *
     * @param itemName    Is the name of the item to be produced
     * @return            the boolean value of the result. 
     */
    public boolean canProduce(String itemName) {
        return type.getRecipeByProductName(itemName) != null;
    }

    
    /**
     * initializes the sources of the building
     *
     * @param sources     Is the list of buildings that provide components of the ingredients of the recipe it produces
     */
    public void setSources(List<Building> sources) {
        this.sources = sources;
    }

   /**
     * A helper function that prints out the string representation of the ingredients which can be used.
     *
     * @return            is the string representation
     */
  private String printStorage() {
    StringBuilder result = new StringBuilder(" storage=[");
    
    if (storage != null && !storage.isEmpty()) {
        Iterator<Map.Entry<String, Integer>> iterator = storage.entrySet().iterator();
        
        while (iterator.hasNext()) {
          Map.Entry<String, Integer> entry = iterator.next();
          String item = entry.getKey();
            int count = entry.getValue();
            result.append(item).append(": ").append(count);

            if (iterator.hasNext()) {
                result.append(", ");
            }
        }
    }

    result.append("]");
    return result.toString();
   }

   //A helper function that returns a string representation of the sources.
  protected String printSources() {
    StringBuilder result = new StringBuilder("[");
    if (sources != null && !sources.isEmpty()) {
        for (int i = 0; i < sources.size(); i++) {
            Building b = sources.get(i);
            result.append(b.getName());
           
            if (i < sources.size() - 1) {
                result.append(", ");
            }
        }
    }
    
    result.append("]");  
    return result.toString();
  }

   /**
     * A helper function that prints out the string representation of number of Requests in the Queue.
     *
     * @return            is the string representation
     */
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

  
   /**
     * Returns the string representation of the Storage and request of the building.
     *
     * @return            is the string representation
     */
  public String printStorageAndRequest(){
    return printStorage() + ",\n" + printRequestQueue();
  }
  
  
   /**
     * returns the number of Requests in the Queue.
     *
     * @return            is the number of requests
     */
    public int getRequestCount() {
        return requestQueue.size();
    }

    // return: still have/no request for this building now.
    public abstract boolean goOneStep();

    protected void update() {
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

    public boolean isNeighbourBuilding(Building b) {
        return isNeighborCoordinate(b.getCoordinate());
    }

    public boolean isNeighborCoordinate(Coordinate c) {
        if (this.getCoordinate() == null || c == null) {
            return false;
        }

        int diffX = Math.abs(this.getCoordinate().x - c.x);
        int diffY = Math.abs(this.getCoordinate().y - c.y);

        if (diffX == 1 && diffY == 0) {
            return true;
        } else if (diffY == 1 && diffX == 0) {
            return true;
        }

        return false;
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

   /**
    * Gets the name of the Building
    *
    * @return    the name of the mine
    */
    public String getName() {
        return name;
    }

   /**
    * Returns the sources of the building.
    *
    * @return    the list of building of sources
    */
    public List<Building> getSources() {
        return sources;
    }

    
   /**
    * Returns the ServePolicy used by the building
    *
    * @return    the Serve Policy
    */
    public ServePolicy getServePolicy(){
      return servePolicy;
    }

   /**
    * Returns the SourcePolicy used by the building
    *
    * @return    the Source Policy
    */
    public SourcePolicy getSourcePolicy(){
      return sourcePolicy;
    }

  
   /**
    * Returns the SourcePolicy used by the building
    *
    * @return    the Source Policy
    */
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
        // System.out.println("in building equals");
        return Objects.equals(name, building.name) && Objects.equals(type, building.type);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }

    public static List<Building> parseSrcList(List<String> srcStr) {
        List<Building> sources = new java.util.ArrayList<>();
        for (String src : srcStr) {
            boolean found = false;
            for (Building b : Building.buildingGlobalList) {
                if (b.getName().equals(src)) {
                    sources.add(b);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("invalid building name :(");
            }
        }

        return sources;
    }

    public List<Request> getRequestQueue() {
        return requestQueue;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getX() {return coordinate.x;}

    public int getY() {return coordinate.y;}

    public BuildingType getBuildingType() {
        return this.type;
    }
}
