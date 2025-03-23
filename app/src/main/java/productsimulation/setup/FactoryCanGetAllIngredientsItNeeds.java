package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FactoryCanGetAllIngredientsItNeeds extends InputRuleChecker {

    public FactoryCanGetAllIngredientsItNeeds(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Build lookup maps for recipes, types, and buildings.
        Map<String, JsonNode> recipes = createLookupMap(root.get("recipes"), "output");
        Map<String, JsonNode> types = createLookupMap(root.get("types"), "name");
        Map<String, JsonNode> buildings = createLookupMap(root.get("buildings"), "name");

        // For each factory building, check if all required ingredients can be sourced.
        for (JsonNode building : root.get("buildings")) {
            if (!building.has("type")) continue;

            String factoryName = building.get("name").asText();
            String typeName = building.get("type").asText();
            JsonNode typeNode = types.get(typeName);

            if (typeNode == null || !typeNode.has("recipes")) continue;

            for (JsonNode recipeNameNode : typeNode.get("recipes")) {
                String recipeName = recipeNameNode.asText();
                JsonNode recipe = recipes.get(recipeName);
                if (recipe == null) continue;

                Iterator<String> ingredients = recipe.get("ingredients").fieldNames();
                while (ingredients.hasNext()) {
                    String ingredient = ingredients.next();
                    if (!building.has("sources")) {
                        return "Factory building '" + factoryName + "' does not have sources but requires ingredient: " + ingredient;
                    }
                    if (!hasSourceForIngredient(building, ingredient, buildings, types)) {
                        return "Factory building '" + factoryName + "' cannot source ingredient '" + ingredient +
                                "' required by recipe '" + recipeName + "'.";
                    }
                }
            }
        }
        return null;
    }

    private Map<String, JsonNode> createLookupMap(JsonNode arrayNode, String keyField) {
        Map<String, JsonNode> map = new HashMap<>();
        for (JsonNode node : arrayNode) {
            map.put(node.get(keyField).asText(), node);
        }
        return map;
    }

    private boolean hasSourceForIngredient(JsonNode building, String ingredient, Map<String, JsonNode> buildings, Map<String, JsonNode> types) {
        for (JsonNode srcNode : building.get("sources")) {
            String srcName = srcNode.asText();
            JsonNode srcBuilding = buildings.get(srcName);
            if (srcBuilding != null && canProduce(srcBuilding, ingredient, types)) {
                return true;
            }
        }
        return false;
    }

    private boolean canProduce(JsonNode building, String ingredient, Map<String, JsonNode> types) {
        // If building is a mine, it produces the ingredient in its "mine" field.
        if (building.has("mine")) {
            return ingredient.equals(building.get("mine").asText());
        }
        // Otherwise, check if the building's type recipes include the ingredient.
        String typeName = building.get("type").asText();
        JsonNode typeNode = types.get(typeName);
            for (JsonNode rec : typeNode.get("recipes")) {
                if (ingredient.equals(rec.asText())) {
                    return true;
                }
            }
        return false;
    }
}
