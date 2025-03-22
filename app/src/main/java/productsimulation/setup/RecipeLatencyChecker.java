package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

public class RecipeLatencyChecker extends InputRuleChecker {

    public RecipeLatencyChecker(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        for (JsonNode recipe : root.get("recipes")) {
            int latency = recipe.get("latency").asInt();
            if (latency < 1) {
                return "Recipe '" + recipe.get("output").asText() + "' has invalid latency: " + latency;
            }
        }
        return null;
    }
}
