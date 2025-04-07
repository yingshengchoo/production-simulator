package productsimulation.setup.json_rules;

import com.fasterxml.jackson.databind.JsonNode;

public class AllRequiredFieldsArePresentAndLegal extends InputRuleChecker {

    public AllRequiredFieldsArePresentAndLegal(InputRuleChecker next) {super(next);}

    private String checkTypes(JsonNode root) {
        if (!root.has("types")) {
            return "Types should be present";
        }

        if (!root.get("types").isArray()) {
            return "Types should be an array";
        }

        for (JsonNode type : root.get("types")) {
            if (!type.has("name")) {
                return "A type should have a name";
            }

            if (!type.has("recipes") || !type.get("recipes").isArray()) {
                return "A type should have a list of recipes";
            }
        }
        return null;
    }

    private String checkBuildings(JsonNode root) {
        if (!root.has("buildings")) {
            return "Buildings should be present";
        }
        if (!root.get("buildings").isArray()) {
            return "Buildings should be an array";
        }
        for (JsonNode building : root.get("buildings")) {
            if (!building.has("name")) {
                return "A building should have a name";
            }
            if (!building.has("type") && !building.has("mine")) {
                return "A building should have a type or a mine";
            }
            if (!building.has("sources")) {
                return "A building should have a source";
            }
            if (!building.get("sources").isArray()) {
                return "A building should have a list of source";
            }
        }
        return null;
    }

    private String checkRecipes(JsonNode root) {
        if (!root.has("recipes")) {
            return "Recipes should be present";
        }

        if (!root.get("recipes").isArray()) {
            return "Recipes should be an array";
        }

        for (JsonNode recipe : root.get("recipes")) {
            if (!recipe.has("output")) {
                return "A recipe should have an output";
            }
            if (!recipe.has("latency") || !recipe.get("latency").isInt()) {
                return "A recipe should have a latency as an int";
            }

            if (!recipe.has("ingredients")) {
                return "A recipe should have ingredients";
            }
        }
        return null;
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        String error = checkTypes(root);
        if (error != null) {
            return error;
        }

        error = checkBuildings(root);
        if (error != null) {
            return error;
        }

        error = checkRecipes(root);
        if (error != null) {
            return error;
        }

        return null;
    }
}
