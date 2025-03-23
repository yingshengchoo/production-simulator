package productsimulation.request.sourcePolicy;

import productsimulation.model.Building;

import java.util.List;

import java.io.Serializable;

public class SoleSourcePolicy implements SourcePolicy, Serializable {
    public Building getSource(List<Building> buildings) {
        return buildings.get(0);
    }
}
