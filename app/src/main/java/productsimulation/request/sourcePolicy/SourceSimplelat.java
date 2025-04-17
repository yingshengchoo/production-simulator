package productsimulation.request.sourcePolicy;

import productsimulation.Log;
import productsimulation.model.Building;
import java.io.Serializable;
import java.util.List;

public class SourceSimplelat implements SourcePolicy, Serializable {

    @Override
    public Building getSource(List<Building> buildings, String ingredient) {

        int min = Integer.MAX_VALUE;
        Building source = null;

        for (Building building : buildings) {
            Log.level2Log("    " + building.getName() + " " + building.getTotalRemainTime());
            if (building.getTotalRemainTime() < min && building.canProduce(ingredient)) {
                min = building.getTotalRemainTime();
                source = building;
            }
        }

        return source;
    }

    @Override
    public String getName() {
        return "simplelat";
    }
}
