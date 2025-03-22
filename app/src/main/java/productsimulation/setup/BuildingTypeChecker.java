package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

import java.util.HashSet;
import java.util.Set;

public class BuildingTypeChecker extends InputRuleChecker {

    public BuildingTypeChecker(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Build set of valid type names.
        Set<String> validTypes = new HashSet<>();
        for (JsonNode type : root.get("types")) {
            validTypes.add(type.get("name").asText());
        }
        // Check each building with a "type" field.
        for (JsonNode building : root.get("buildings")) {
            if (building.has("type")) {
                String t = building.get("type").asText();
                if (!validTypes.contains(t)) {
                    return "Building '" + building.get("name").asText() + "' has unknown type: " + t;
                }
            }
        }
        return null;
    }
}
