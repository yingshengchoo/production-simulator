package productsimulation.model;

import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.request.Request;
import productsimulation.request.RequestStatus;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.util.List;

public class Factory extends Building {

    /**
     * Constructs a Factory with the specified name, type, sources, and policies.
     *
     * @param name         is the coordinate of the top left of the ship.
     * @param type         is the Building Type.
     * @param sources      is list of buildings that provides the ingredients to make the recipes.
     * @param sourcePolicy is the policy that the building uses to select between sources.
     * @param servePolicy  is the policy that the building uses to select between requests.
     */
    public Factory(String name, BuildingType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy){
        super(name, type, sources, sourcePolicy, servePolicy);
    }

    public Factory(String name, BuildingType type, SourcePolicy sourcePolicy, ServePolicy servePolicy){
        super(name, type, sourcePolicy, servePolicy);
    }

    public Factory(String name, BuildingType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy, Coordinate coordinate){
        super(name, type, sources, sourcePolicy, servePolicy, coordinate);
    }

    public Factory register() {
        buildingGlobalList.add(this);
        Board.getBoard().addBuilding(this);
        return this;
    }

    public static Factory addFactory(String name, List<Building> sources, SourcePolicy sourcePolicy,
                                     ServePolicy servePolicy, Coordinate coordinate, BuildingType type) {
        Board board = Board.getBoard();
        int weight = board.getBoardPosWeight(coordinate);
        if (weight == 1 || weight == Integer.MAX_VALUE) {
            throw new RuntimeException("invalid coordinate!");
        }

        return new Factory(name, type, sources, sourcePolicy, servePolicy, coordinate).register();
    }


    public boolean goOneStep() {

        if(currentRequest == null) {
            if(!requestQueue.isEmpty()) {
//                [recipe selection]: Hw2 has fifo on cycle 8
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

                Log.level2Log("    request:[" + name + ":" + request.getIngredient() + ":"
                        + request.getRequesterName() + "] is chosen");
                if(request.getStatus().equals(RequestStatus.READY)) {
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

    // Returns the String representation of Factory.
    @Override
    public String toString() {
        return "Factory\n{name='" + super.name +
                "',\n type='" + super.type.getName() +
                "',\n sources=" + printSources() +
                // ",\n sourcePolicy=" + super.sourcePolicy.toString() +
                // "',\n servePolicy=" + super.servePolicy.toString() + "'" +
                ",\n" + printStorageAndRequest()+
                "\n}";
    }
}
