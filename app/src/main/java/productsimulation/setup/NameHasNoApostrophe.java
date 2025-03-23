package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

public class NameHasNoApostrophe extends InputRuleChecker {

    public NameHasNoApostrophe(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        for (JsonNode recipe : root.get("recipes")) {
            String output = recipe.get("output").asText();
            if (output.contains("'")) {
                return "Recipe output name contains illegal character: " + output;
            }
        }
        for (JsonNode type : root.get("types")) {
            String name = type.get("name").asText();
            if (name.contains("'")) {
                return "Type name contains illegal character: " + name;
            }
        }
        for (JsonNode building : root.get("buildings")) {
            String name = building.get("name").asText();
            if (name.contains("'")) {
                return "Building name contains illegal character: " + name;
            }
        }
        return null;
    }
}
