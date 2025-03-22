package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

public class MineRecipeChecker extends InputRuleChecker {

    public MineRecipeChecker(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // For each mine building, check that its referenced recipe has no ingredients.
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
