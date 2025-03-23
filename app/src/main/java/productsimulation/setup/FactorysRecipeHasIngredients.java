package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

public class FactorysRecipeHasIngredients extends InputRuleChecker {

    public FactorysRecipeHasIngredients(InputRuleChecker next) {
        super(next);
    }
    @Override
    protected String checkMyRule(JsonNode root) {
        JsonNode typesNode = root.get("types");
        if (typesNode == null || !typesNode.isArray()) {
            return null;
        }
        for (JsonNode type : typesNode) {
            String typeName = type.get("name").asText();
            for (JsonNode recName : type.get("recipes")) {
                String recipeName = recName.asText();
                boolean foundRecipe = false;
                JsonNode recipesNode = root.get("recipes");
                if (recipesNode == null || !recipesNode.isArray()) {
                    return "Recipes field is missing or invalid.";
                }
                for (JsonNode recipe : recipesNode) {
                    if (recipe.get("output").asText().equals(recipeName)) {
                        foundRecipe = true;
                        // Debug: print the ingredients node
                        System.out.println("Checking recipe '" + recipeName + "': "
                                + recipe.get("ingredients").toString());
                        if (recipe.get("ingredients").size() == 0) {
                            return "Factory type '" + typeName + "' references recipe '" + recipeName +
                                    "' which has no ingredients.";
                        }
                    }
                }
                if (!foundRecipe) {
                    return "Factory type '" + typeName + "' references undefined recipe '" + recipeName + "'.";
                }
            }
        }
        return null;
    }

}
