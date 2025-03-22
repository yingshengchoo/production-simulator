package productsimulation.request.sourcePolicy;

import productsimulation.model.Building;

import java.util.List;

public class SoleSourcePolicy implements SourcePolicy {
    public Building getSource(List<Building> buildings) {
        return buildings.get(0);
    }
}
