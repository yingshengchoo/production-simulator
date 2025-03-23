package productsimulation.request.sourcePolicy;

import productsimulation.model.Building;

import java.util.ArrayList;
import java.util.List;

public interface SourcePolicy {
    Building getSource(List<Building> buildings, String ingredient);

    static List<Building> sourceFilter(List<Building> buildings, String ingredient) {
        List<Building> sourcesAfterFilter = new ArrayList<>();
        for(Building b: buildings) {
            if(b.canProduce(ingredient)) {
                sourcesAfterFilter.add(b);
            }
        }
        return sourcesAfterFilter;
    }
}
