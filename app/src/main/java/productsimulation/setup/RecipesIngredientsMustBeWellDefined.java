package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RecipesIngredientsMustBeWellDefined extends InputRuleChecker {

    public RecipesIngredientsMustBeWellDefined(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Build set of valid recipe names.
        Set<String> validRecipes = new HashSet<>();
        for (JsonNode recipe : root.get("recipes")) {
            validRecipes.add(recipe.get("output").asText());
        }
        // For each recipe, check that every ingredient key is defined.
        for (JsonNode recipe : root.get("recipes")) {
            String output = recipe.get("output").asText();
            JsonNode ingredients = recipe.get("ingredients");
            Iterator<String> keys = ingredients.fieldNames();
            while (keys.hasNext()) {
                String ingredient = keys.next();
                if (!validRecipes.contains(ingredient)) {
                    return "Recipe '" + output + "' requires ingredient '" + ingredient + "', which is not defined.";
                }
            }
        }
        return null;
    }
}
