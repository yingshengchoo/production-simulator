package productsimulation.model.waste;

import productsimulation.model.BuildingType;
import productsimulation.model.Recipe;

import java.util.HashMap;
import java.util.Map;

public class WasteDisposalType extends BuildingType {
    public final Map<String, WasteDisposal.WasteConfig> configs = new HashMap<>();

    public WasteDisposalType(String name, Map<String, Recipe> recipes) {
        super(name, recipes);
    }

    public WasteDisposalType(String name, Map<String, Recipe> recipes, Map<String, int[]> configMap) {
        super(name, recipes);
        for (Map.Entry<String, int[]> e : configMap.entrySet()) {
            int[] arr = e.getValue();
            configs.put(e.getKey(), new WasteDisposal.WasteConfig(arr[0], arr[1], arr[2]));
        }
    }
}
