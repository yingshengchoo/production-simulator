package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

public class IllegalCharacterChecker extends InputRuleChecker {

    public IllegalCharacterChecker(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Check recipes
        for (JsonNode recipe : root.get("recipes")) {
            String output = recipe.get("output").asText();
            if (output.contains("'")) {
                return "Recipe output name contains illegal character: " + output;
            }
        }
        // Check types
        for (JsonNode type : root.get("types")) {
            String name = type.get("name").asText();
            if (name.contains("'")) {
                return "Type name contains illegal character: " + name;
            }
        }
        // Check buildings
        for (JsonNode building : root.get("buildings")) {
            String name = building.get("name").asText();
            if (name.contains("'")) {
                return "Building name contains illegal character: " + name;
            }
        }
        return null;
    }
}
