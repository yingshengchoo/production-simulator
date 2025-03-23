package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Set;

public class SourceBuildingAreWellDefined extends InputRuleChecker {

    public SourceBuildingAreWellDefined(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Collect all building names.
        Set<String> buildingNames = new HashSet<>();
        for (JsonNode building : root.get("buildings")) {
            buildingNames.add(building.get("name").asText());
        }
        // Check that every source reference exists.
        for (JsonNode building : root.get("buildings")) {
            if (building.has("sources")) {
                for (JsonNode src : building.get("sources")) {
                    String s = src.asText();
                    if (!buildingNames.contains(s)) {
                        return "Building '" + building.get("name").asText() + "' references unknown source: " + s;
                    }
                }
            }
        }
        return null;
    }
}
