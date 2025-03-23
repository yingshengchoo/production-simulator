package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

import java.util.HashSet;
import java.util.Set;

public class RecipeAndTypesAndBuildingsHaveUniqueNames extends InputRuleChecker {

    public RecipeAndTypesAndBuildingsHaveUniqueNames(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        Set<String> recipeNames = new HashSet<>();
        for (JsonNode recipe : root.get("recipes")) {
            String output = recipe.get("output").asText();
            if (recipeNames.contains(output)) {
                return "Duplicate recipe name: " + output;
            }
            recipeNames.add(output);
        }

        Set<String> typeNames = new HashSet<>();
        for (JsonNode type : root.get("types")) {
            String name = type.get("name").asText();
            if (typeNames.contains(name)) {
                return "Duplicate type name: " + name;
            }
            typeNames.add(name);
        }

        Set<String> buildingNames = new HashSet<>();
        for (JsonNode building : root.get("buildings")) {
            String name = building.get("name").asText();
            if (buildingNames.contains(name)) {
                return "Duplicate building name: " + name;
            }
            buildingNames.add(name);
        }
        return null;
    }
}
