package productsimulation.request.sourcePolicy;

import productsimulation.model.Building;

import java.util.List;

import java.io.Serializable;

public class SourceQLen implements SourcePolicy, Serializable {

    @Override
    public Building getSource(List<Building> buildings, String ingredient) {
        buildings = SourcePolicy.sourceFilter(buildings, ingredient);

        int min = Integer.MAX_VALUE;
        Building source = null;

        for (Building building : buildings) {
            if (building.getRequestCount() < min) {
                min = building.getRequestCount();
                source = building;
            }
        }

        return source;
    }
}
