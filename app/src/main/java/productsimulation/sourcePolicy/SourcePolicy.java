package productsimulation.sourcePolicy;

import productsimulation.model.Building;

import java.util.List;

public interface SourcePolicy {
    Building getSource(List<Building> buildings);
}
