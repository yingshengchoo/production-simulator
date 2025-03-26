package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.Log;

public class FactorysRecipeHasIngredients extends InputRuleChecker {

    public FactorysRecipeHasIngredients(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        JsonNode typesNode = root.get("types");
        JsonNode recipesNode = root.get("recipes");

        // If "types" is missing or not an array, no rule checking is needed.
        if (typesNode == null || !typesNode.isArray()) {
            return null;
        }

        // Validate the "recipes" field once.
        if (recipesNode == null || !recipesNode.isArray()) {
            return "Recipes field is missing or invalid.";
        }

        // Iterate through each factory type.
        for (JsonNode type : typesNode) {
            String typeName = type.get("name").asText();

            // Iterate through each recipe name referenced by the type.
            for (JsonNode recName : type.get("recipes")) {
                String recipeName = recName.asText();
                JsonNode recipe = findRecipeByOutput(recipesNode, recipeName);

                if (recipe == null) {
                    return "Factory type '" + typeName + "' references undefined recipe '" + recipeName + "'.";
                }

                // Debug print the ingredients.
                Log.debugLog("Checking recipe '" + recipeName + "': " + recipe.get("ingredients"));

                if (recipe.get("ingredients").size() == 0) {
                    return "Factory type '" + typeName + "' references recipe '" + recipeName + "' which has no ingredients.";
                }
            }
        }
        return null;
    }

    // Helper method to find a recipe by its output name.
    private JsonNode findRecipeByOutput(JsonNode recipesNode, String recipeName) {
        for (JsonNode recipe : recipesNode) {
            if (recipe.get("output").asText().equals(recipeName)) {
                return recipe;
            }
        }
        return null;
    }
}
