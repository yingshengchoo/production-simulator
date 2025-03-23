package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;

public class MineHasEmptySources extends InputRuleChecker {

    public MineHasEmptySources(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
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
