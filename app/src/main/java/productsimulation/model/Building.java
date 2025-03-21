package productsimulation.model;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.RequestBroadcaster;
import productsimulation.request.Request;
import productsimulation.request.RequestStatus;
import productsimulation.request.ServePolicy;
import productsimulation.sourcePolicy.SourcePolicy;

import java.util.*;

public abstract class Building {
    protected final String name;
    protected FactoryType type;
    protected Request currentRequest;
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

    // 按recipe顺序dfs传播request
    public void addRequest(Request request) {
        Log.debugLog("adding request: " + request.getIngredient() + " to " + name);
        requestQueue.add(request);
        Recipe recipe = type.getRecipeByProductName(request.getIngredient());
        Map<String, Integer> ingredients = recipe.getIngredients();
        for(String ingredient: ingredients.keySet()) {
            int num = ingredients.get(ingredient);
            for(int i = 0; i < num; i++) {
                // todo sourcePolicy.getSource() 除了备选列表，还要有品名
                Building chosenSource = sourcePolicy.getSource(sources);
                Request req = new Request(ingredient, recipe,this);
                chosenSource.addRequest(req);
            }
        }
    }

    // return: still have/no request for this building now.
    private boolean goOneStep() {
        if(currentRequest == null) {
            if(!requestQueue.isEmpty()) {
                Request request = servePolicy.getRequest(requestQueue);
                // 如果request原料齐备，可以开工
                // 如果goOneStep导致一个request正好完成，那需要相应更新request队列
                request.updateStatus(storage);
                if(request.getStatus() == RequestStatus.READY) {
                    request.readyToWorking();
                }
                currentRequest = request;
            } else {
                Log.debugLog("no request here: " + name);
                return true;
            }
        }

        // 实际工作用remainTimeMinusOne模拟
        Log.debugLog("processing request: " +
                currentRequest.getIngredient() + ", " + currentRequest.getRemainTime());
        currentRequest.remainTimeMinusOne();
        return false;
    }

    private void update() {
        if(currentRequest != null && currentRequest.getRemainTime() == 0) {
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

    public void changePolicy() {
    }

    public boolean notified() {
        return goOneStep();
    }

    public void updateNotified() {
        update();
    }

    public void accept() {
    }

    public String getName() {
        return name;
    }
}
