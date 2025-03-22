package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import productsimulation.InputRuleChecker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FactorySourceAvailabilityChecker extends InputRuleChecker {

    public FactorySourceAvailabilityChecker(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Build lookup maps.
        Map<String, JsonNode> recipeMap = new HashMap<>();
        for (JsonNode recipe : root.get("recipes")) {
            recipeMap.put(recipe.get("output").asText(), recipe);
        }
        Map<String, JsonNode> typeMap = new HashMap<>();
        for (JsonNode type : root.get("types")) {
            typeMap.put(type.get("name").asText(), type);
        }
        Map<String, JsonNode> buildingMap = new HashMap<>();
        for (JsonNode building : root.get("buildings")) {
            buildingMap.put(building.get("name").asText(), building);
        }

        // For each factory (building with a "type" field), verify that every ingredient (from every recipe in its type)
        // can be produced by at least one source.
        for (JsonNode building : root.get("buildings")) {
            if (building.has("type")) {
                String buildingName = building.get("name").asText();
                String typeName = building.get("type").asText();
                JsonNode typeNode = typeMap.get(typeName);
                // If typeNode is null or does not have "recipes", skip checking this building.
                // BuildingTypeChecker will determine this is illegal then.
                if (typeNode == null || !typeNode.has("recipes")) {
                    continue;
                }
                for (JsonNode recNameNode : typeNode.get("recipes")) {
                    String recipeName = recNameNode.asText();
                    JsonNode recipeNode = recipeMap.get(recipeName);
                    if (recipeNode == null) continue;
                    Iterator<String> ingredientKeys = recipeNode.get("ingredients").fieldNames();
                    while (ingredientKeys.hasNext()) {
                        String ingredient = ingredientKeys.next();
                        boolean found = false;
                        if (!building.has("sources")) {
                            return "Factory building '" + buildingName + "' does not have sources but requires ingredient: " + ingredient;
                        }
                        for (JsonNode srcNameNode : building.get("sources")) {
                            String srcName = srcNameNode.asText();
                            JsonNode srcBuilding = buildingMap.get(srcName);
                            if (srcBuilding == null) continue;
                            // Use the modified canProduce() method.
                            if (canProduce(srcBuilding, ingredient, typeMap)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            return "Factory building '" + buildingName + "' cannot source ingredient '" + ingredient +
                                    "' required by recipe '" + recipeName + "'.";
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines whether a source building can produce the given ingredient.
     * For mines, the "mine" field indicates the output.
     * For factories, if the building's type (obtained from typeMap) has a recipe that produces the ingredient,
     * then it can produce it.
     *
     * @param building the source building JsonNode.
     * @param ingredient the ingredient name.
     * @param typeMap a map from type names to their corresponding JsonNode.
     * @return true if the building can produce the ingredient; false otherwise.
     */
    private boolean canProduce(JsonNode building, String ingredient, Map<String, JsonNode> typeMap) {
        if (building.has("mine")) {
            String mineOutput = building.get("mine").asText();
            return ingredient.equals(mineOutput);
        }
        if (building.has("type")) {
            String typeName = building.get("type").asText();
            JsonNode typeNode = typeMap.get(typeName);
            if (typeNode != null && typeNode.has("recipes")) {
                for (JsonNode recName : typeNode.get("recipes")) {
                    if (ingredient.equals(recName.asText())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
