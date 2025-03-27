package productsimulation;

import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.Request;
import java.io.Serializable;
import java.util.*;

public class RequestBroadcaster implements Serializable {
    private static RequestBroadcaster instance = new RequestBroadcaster();
    public static RequestBroadcaster getInstance() {
        return instance;
    }

    private final Map<String, Building> buildings;
    private final Map<String, Recipe> recipes;

    /**
     * private, for singleton
     */
    private RequestBroadcaster() {
        buildings = new HashMap<>();
        recipes = new HashMap<>();
    }

    // only for test
    public int getBuildingsSize() {
        return buildings.size();
    }

    public void addBuildings(Building b) {
        buildings.put(b.getName(), b);
    }

    public void removeBuildings(Building b) {
        buildings.remove(b.getName());
    }

    // only for test
    public int getRecipesSize() {
        return recipes.size();
    }

    public void addRecipes(Recipe r) { recipes.put(r.getOutput(), r); }

    public void removeRecipes(Recipe r) {
        recipes.remove(r.getOutput());
    }

    public void accept(StateVisitor v){
      v.visit(this);
    }

    public void loadRequestBroadcaster(RequestBroadcaster rb){
      instance = rb;
    }

    public void userRequestHandler(String itemName, String buildingName) {
        Recipe r = recipes.get(itemName);
        if(r != null) {
            Request request = new Request(itemName, r, null);
            if(buildings.containsKey(buildingName)) {
                Building b = buildings.get(buildingName);
                b.addRequest(request);
            }
        }
    }

  public void reset(){
    recipes.clear();
    buildings.clear();
  }
}
