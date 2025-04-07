package productsimulation.setup.json_rules;

import com.fasterxml.jackson.databind.JsonNode;

public class MinesRecipeHasEmptyIngredients extends InputRuleChecker {

    public MinesRecipeHasEmptyIngredients(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        for (JsonNode building : root.get("buildings")) {
            if (building.has("mine")) {
                String mineRecipeName = building.get("mine").asText();
                for (JsonNode recipe : root.get("recipes")) {
                    if (recipe.get("output").asText().equals(mineRecipeName)) {
                        if (recipe.get("ingredients").size() > 0) {
                            return "Mine building '" + building.get("name").asText() + "' references recipe '" +
                                    mineRecipeName + "' which should have no ingredients.";
                        }
                    }
                }
            }
        }
        return null;
    }
}
