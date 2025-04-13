package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import productsimulation.model.BuildingType;
import productsimulation.model.Recipe;
import productsimulation.model.StorageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TypeParser parses the "types" JSON array from an input.
 * {
 *   "types": [
 *     {
 *       "name": "Bolt Storage (100)",
 *       "type": "storage",
 *       "info": {
 *         "stores": "bolt",
 *         "capacity": 100,
 *         "priority": 1.7
 *       }
 *     },
 *     {
 *       "name": "Door Factory",
 *       "type": "factory",
 *       "info": {
 *         "recipes": [ "door", "bolt" ]
 *       }
 *     }
 *   ]
 * }
 *
 * For storage types, a StorageType instance is created.
 * For factory types, a BuildingType is created by mapping the listed recipe names to Recipe instances.
 * This parser only verifies that the minimal required parameters exist.
 */
public class TypeParser {
    // Map to store parsed building types keyed by their names.
    private final Map<String, BuildingType> typeMap;

    public TypeParser() {
        this.typeMap = new LinkedHashMap<>();
    }

    /**
     * Parses the building types from the given JSON BufferedReader.
     *
     * @param reader The BufferedReader reading the JSON input.
     * @return null if parsing is successful; otherwise, an error message is returned.
     */
    public String parse(BufferedReader reader) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Read the entire JSON tree.
            JsonNode root = mapper.readTree(reader);
            // Get the "types" node.
            JsonNode typesNode = root.get("types");
            if (typesNode == null || !typesNode.isArray()) {
                return "Error: 'types' field is missing or is not an array.";
            }

            // Iterate over each type definition.
            for (JsonNode typeNode : typesNode) {
                String typeName = typeNode.get("name").asText();
                // Determine type category ("storage" or "factory"); use lower-case for simplicity.
                String category = typeNode.get("type").asText().toLowerCase();
                // Get the "info" object.
                JsonNode infoNode = typeNode.get("info");

                if ("storage".equals(category)) {
                    // Ensure that the necessary parameters exist.
                    if (infoNode == null ||
                            !infoNode.has("stores") ||
                            !infoNode.has("capacity") ||
                            !infoNode.has("priority")) {
                        return "Error: Storage type '" + typeName +
                                "' must have 'stores', 'capacity', and 'priority' in its 'info' field.";
                    }
                    String stores = infoNode.get("stores").asText();
                    int capacity = infoNode.get("capacity").asInt();
                    double priority = infoNode.get("priority").asDouble();

                    // Create a StorageType instance.
                    StorageType storageType = new StorageType(typeName, priority, capacity, stores);
                    typeMap.put(typeName, storageType);
                } else if ("factory".equals(category)) {
                    // For factory types, ensure there is a recipes array.
                    if (infoNode == null || !infoNode.has("recipes") || !infoNode.get("recipes").isArray()) {
                        return "Error: Factory type '" + typeName +
                                "' must have a 'recipes' array in its 'info' field.";
                    }
                    Map<String, Recipe> recipesForType = new LinkedHashMap<>();
                    // Iterate over recipe names.
                    for (JsonNode recipeNameNode : infoNode.get("recipes")) {
                        String recipeName = recipeNameNode.asText();
                        // Retrieve the recipe; must have been set up previously.
                        Recipe recipe = Recipe.getRecipe(recipeName);
                        if (recipe == null) {
                            return "Error: Factory type '" + typeName +
                                    "' references an unknown recipe '" + recipeName + "'.";
                        }
                        recipesForType.put(recipeName, recipe);
                    }
                    BuildingType factoryType = new BuildingType(typeName, recipesForType);
                    typeMap.put(typeName, factoryType);
                } else {
                    return "Error: Unknown building type category '" + category +
                            "' for type '" + typeName + "'.";
                }
            }
        } catch (IOException e) {
            return "IOException: " + e.getMessage();
        }
        return null;
    }

    /**
     * Returns an unmodifiable list of the parsed building types.
     *
     * @return List of building types.
     */
    public List<BuildingType> getTypeMap() {
        return new ArrayList<>(typeMap.values());
    }
}
