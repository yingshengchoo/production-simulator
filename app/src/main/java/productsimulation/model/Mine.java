
package productsimulation.model;

import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.request.Request;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import java.util.List;

public class Mine extends Building {
    /**
     * Constructs a Mine with the specified name, type, sources, and policies.
     *
     * @param name         is the name of the Mine.
     * @param type         is the Building Type.
     * @param sources      is list of buildings that provides the ingredients to make the recipes.
     * @param sourcePolicy is the policy that the building uses to select between sources.
     * @param servePolicy  is the policy that the building uses to select between requests.
     */
    public Mine(String name, BuildingType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy){
        super(name, type, sources, sourcePolicy, servePolicy);
    }

    public static Mine addMine(String name, List<Building> sources, SourcePolicy sourcePolicy,
                               ServePolicy servePolicy, Coordinate coordinate, BuildingType type) {
        Board board = Board.getBoard();
        int weight = board.getBoardPosWeight(coordinate);
        if (weight == 1 || weight == Integer.MAX_VALUE) {
            throw new RuntimeException("invalid coordinate!");
        }
        
        //removed register building --> construct first. After construction then register.
        Mine mine = new Mine(name, type, sources, sourcePolicy, servePolicy, coordinate);
        BuildingCostHandler.constructBuilding(mine);
        return mine;
    }

    /**
     * Constructs a Mine with the specified name, type, and policies.
     *
     * @param name         is the name of the Mine.
     * @param type         is the Building Type.
     * @param sourcePolicy is the policy that the building uses to select between sources.
     * @param servePolicy  is the policy that the building uses to select between requests.
     */

    public Mine(String name, BuildingType type, SourcePolicy sourcePolicy, ServePolicy servePolicy){
        super(name, type, sourcePolicy, servePolicy);
    }

    public Mine(String name, BuildingType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy, Coordinate coordinate){
        super(name, type, sources, sourcePolicy, servePolicy, coordinate);
    }

    public Mine register() {
        buildingGlobalList.add(this);
        Board.getBoard().addBuilding(this);
        return this;
    }

    public boolean goOneStep() {
        if(currentRequest == null) {
            if(!requestQueue.isEmpty()) {
                Request request = servePolicy.getRequest(requestQueue);
                // 相比factory，mine不需要等待原材料/检查库存
                currentRequest = request;
                request.readyToWorking(storage);
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

    // Returns the String representation of Mine.
    @Override
    public String toString() {
        return "Mine\n{name='" + super.name +
                "',\n mine='" + super.type.getName() +
                "',\n sources=" + printSources() +
                // ",\n sourcePolicy=" + super.sourcePolicy.toString() +
                // ",\n servePolicy=" + super.servePolicy.toString() + "'" +
                ",\n" + printStorageAndRequest() +
                "\n}";
    }


}
