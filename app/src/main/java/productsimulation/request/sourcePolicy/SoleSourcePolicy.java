package productsimulation.request.sourcePolicy;

import productsimulation.model.Building;

import java.util.List;
import java.io.Serializable;

public class SoleSourcePolicy implements SourcePolicy, Serializable {
    public Building getSource(List<Building> buildings, String ingredient) {
        buildings = SourcePolicy.sourceFilter(buildings, ingredient);
        return buildings.get(0);
    }

    @Override
    public String getName() {
        return "sole";
    }
}
