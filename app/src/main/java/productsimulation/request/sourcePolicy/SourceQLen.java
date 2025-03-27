package productsimulation.request.sourcePolicy;

import productsimulation.Log;
import productsimulation.model.Building;

import java.util.List;

import java.io.Serializable;

public class SourceQLen implements SourcePolicy, Serializable {

    @Override
    public Building getSource(List<Building> buildings, String ingredient) {

        int min = Integer.MAX_VALUE;
        Building source = null;

        for (Building building : buildings) {
            if (building.canProduce(ingredient)) {
                Log.level2Log("    " + building.getName() + " " + building.getRequestCount());
                if (building.getRequestCount() < min) {
                    min = building.getRequestCount();
                    source = building;
                }
            }
        }

        return source;
    }

    @Override
    public String getName() {
        return "qlen";
    }
}
