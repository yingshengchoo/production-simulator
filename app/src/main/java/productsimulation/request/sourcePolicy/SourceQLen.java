package productsimulation.request.sourcePolicy;

import productsimulation.model.Building;

import java.util.List;

public class SourceQLen implements SourcePolicy {

    @Override
    public Building getSource(List<Building> buildings) {
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
