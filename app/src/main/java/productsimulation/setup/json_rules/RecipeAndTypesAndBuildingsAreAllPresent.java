package productsimulation.setup.json_rules;

import com.fasterxml.jackson.databind.JsonNode;

public class RecipeAndTypesAndBuildingsAreAllPresent extends InputRuleChecker {

    public RecipeAndTypesAndBuildingsAreAllPresent(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        if (!root.has("recipes") || !root.has("types") || !root.has("buildings")) {
            return "JSON file must contain 'recipes', 'types', and 'buildings' fields.";
        }
        return null;
    }
}
