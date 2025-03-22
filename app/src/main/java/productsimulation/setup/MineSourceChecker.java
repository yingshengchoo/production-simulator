package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

public class MineSourceChecker extends InputRuleChecker {

    public MineSourceChecker(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // For each mine building, ensure that sources is not present or empty.
        for (JsonNode building : root.get("buildings")) {
            if (building.has("mine")) {
                if (building.has("sources")) {
                    if (building.get("sources").isArray() && building.get("sources").size() > 0) {
                        return "Mine building '" + building.get("name").asText() + "' must not have non-empty 'sources'.";
                    }
                }
            }
        }
        return null;
    }
}
