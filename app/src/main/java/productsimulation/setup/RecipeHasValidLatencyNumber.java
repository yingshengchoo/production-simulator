package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

public class RecipeHasValidLatencyNumber extends InputRuleChecker {

    public RecipeHasValidLatencyNumber(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        for (JsonNode recipe : root.get("recipes")) {
            JsonNode latencyNode = recipe.get("latency");
            if (!latencyNode.canConvertToInt()) {
                return "Recipe '" + recipe.get("output").asText()
                        + "' has invalid latency (out of int range): " + latencyNode.asText();
            }

            int latency = latencyNode.asInt();
            if (latency < 1) {
                return "Recipe '" + recipe.get("output").asText() + "' has invalid latency: " + latency;
            }
        }
        return null;
    }
}
