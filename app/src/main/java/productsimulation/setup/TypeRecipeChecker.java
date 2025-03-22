package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

import java.util.HashSet;
import java.util.Set;

public class TypeRecipeChecker extends InputRuleChecker {

    public TypeRecipeChecker(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Build set of valid recipe names.
        Set<String> validRecipes = new HashSet<>();
        for (JsonNode recipe : root.get("recipes")) {
            validRecipes.add(recipe.get("output").asText());
        }
        // For each type, ensure that every recipe it references exists.
        for (JsonNode type : root.get("types")) {
            for (JsonNode recName : type.get("recipes")) {
                String rec = recName.asText();
                if (!validRecipes.contains(rec)) {
                    return "Type '" + type.get("name").asText() + "' references unknown recipe: " + rec;
                }
            }
        }
        return null;
    }
}
