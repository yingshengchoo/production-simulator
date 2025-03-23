package productsimulation.model;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.request.Request;
import productsimulation.request.RequestStatus;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.util.*;

public abstract class Building {
    protected final String name;
    protected FactoryType type;
    protected Request currentRequest;
    protected int currentRemainTime;
    protected List<Request> requestQueue;
    protected Map<String, Integer> storage;
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
        Log.debugLog("adding request: " + request.getIngredient() + " to " + name);
        requestQueue.add(request);
        Recipe parentRecipe = type.getRecipeByProductName(request.getIngredient());
        Map<String, Integer> ingredients = parentRecipe.getIngredients();
        for(String ingredient: ingredients.keySet()) {
            int num = ingredients.get(ingredient);
            for(int i = 0; i < num; i++) {
                Building chosenSource = sourcePolicy.getSource(sources, ingredient);
                Recipe childRecipe = chosenSource.type.getRecipeByProductName(ingredient);
                Request req = new Request(ingredient, childRecipe, this);
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


    public int getRequestCount() {
        return requestQueue.size();
    }

    // return: still have/no request for this building now.
    protected abstract boolean goOneStep();

    private void update() {
        if(currentRequest != null && currentRemainTime == 0) {
            Log.debugLog("request done: " +
                    currentRequest.getIngredient() +
                    " at time " + LogicTime.getInstance().getStep() +
                    " at place " + name);
            currentRequest.doneReportAndTransport();
            requestQueue.remove(currentRequest);
            currentRequest = null;
        }
    }

    public void updateStorage(String itemName) {
        storage.put(itemName, storage.getOrDefault(itemName, 0) + 1);
    }

    public void changeServePolicy(ServePolicy servePolicy) {
        this.servePolicy = servePolicy;
    }

    public void changeSourcePolicy(SourcePolicy sourcePolicy) {
        this.sourcePolicy = sourcePolicy;
    }

    public boolean notified() {
        return goOneStep();
    }

    public void updateNotified() {
        update();
    }

    public void accept(BuildingVisitor visitor) {
    }

    public String getName() {
        return name;
    }
}
