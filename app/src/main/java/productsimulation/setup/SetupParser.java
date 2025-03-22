package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import productsimulation.*;
import productsimulation.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


/**
 * SetupParser reads and validates the simulation setup JSON file.
 */
public class SetupParser {
    InputRuleChecker inputRuleChecker;

    // Maps to hold the simulation objects.
    private final Map<String, Recipe> recipeMap;
    private final Map<String, FactoryType> typeMap;
    private final Map<String, Building> buildingMap;

    public SetupParser() {
        recipeMap = new HashMap<>();
        typeMap = new HashMap<>();
        buildingMap = new HashMap<>();

        // Build the chain-of-responsibility.
        inputRuleChecker =
                new RequiredFieldsChecker(
                        new UniqueNamesChecker(
                                new IllegalCharacterChecker(
                                        new BuildingTypeChecker(
                                                new SourcesDefinedChecker(
                                                        new MineSourceChecker(
                                                                new TypeRecipeChecker(
                                                                        new RecipeIngredientsChecker(
                                                                                new FactoryRecipeChecker(
                                                                                        new MineRecipeChecker(
                                                                                                new RecipeLatencyChecker(
                                                                                                        new FactorySourceAvailabilityChecker(null)
                                                                                                )))))))))));
    }

    /**
     * Reads the JSON from the provided BufferedReader and returns the root JsonNode.
     *
     * @param reader the BufferedReader for the JSON file.
     * @return the root JsonNode or null if parsing fails.
     */
    private JsonNode parseJson(BufferedReader reader) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * First, the JSON is read and the input is validated by the chain of rule checkers.
     * If validation passes, the JSON is parsed into recipes, types, and buildings.
     *
     * @param r the BufferedReader for the JSON file.
     */
    public void parse(BufferedReader r) {
        JsonNode root = parseJson(r);
        if (root == null) {
            System.err.println("Failed to parse JSON file.");
            return;
        }

        String error = inputRuleChecker.checkInput(root);
        if (error != null) {
            System.err.println("Input validation error: " + error);
            return;
        }

        if (!parseRecipes(root.get("recipes"))) return;
        if (!parseTypes(root.get("types"))) return;
        if (!parseBuildings(root.get("buildings"))) return;
        if (!assignBuildingSources(root.get("buildings"))) return;

        System.out.println("Parsing completed successfully.");
        System.out.println("Recipes: " + recipeMap.size());
        System.out.println("Types: " + typeMap.size());
        System.out.println("Buildings: " + buildingMap.size());
    }

    private boolean parseRecipes(JsonNode recipesNode) {
        if (!recipesNode.isArray()) {
            System.err.println("'recipes' field must be an array.");
            return false;
        }
        for (JsonNode recipeNode : recipesNode) {
            if (!recipeNode.has("output") || !recipeNode.has("ingredients") || !recipeNode.has("latency")) {
                System.err.println("A recipe is missing required fields (output, ingredients, latency).");
                return false;
            }
            String output = recipeNode.get("output").asText();
            int latency = recipeNode.get("latency").asInt();
            Map<String, Integer> ingredients = new HashMap<>();
            Iterator<String> fields = recipeNode.get("ingredients").fieldNames();
            while (fields.hasNext()) {
                String key = fields.next();
                ingredients.put(key, recipeNode.get("ingredients").get(key).asInt());
            }
            Recipe recipe = new Recipe(latency, ingredients);
            recipeMap.put(output, recipe);
        }
        return true;
    }

    private boolean parseTypes(JsonNode typesNode) {
        // Check that the types node is an array.
        if (!typesNode.isArray()) {
            System.err.println("'types' field must be an array.");
            return false;
        }
        // Iterate over each type definition in the JSON.
        for (JsonNode typeNode : typesNode) {
            // Each type must have a "name" and a "recipes" field.
            if (!typeNode.has("name") || !typeNode.has("recipes")) {
                System.err.println("A type is missing required fields (name, recipes).");
                return false;
            }
            String typeName = typeNode.get("name").asText();
            // Build a map for recipes corresponding to this type.
            // This map's keys are the recipe output names, and values are the Recipe objects.
            Map<String, Recipe> recipesForType = new HashMap<>();
            for (JsonNode recName : typeNode.get("recipes")) {
                String recipeName = recName.asText();
                Recipe rcp = recipeMap.get(recipeName);
                if (rcp != null) {
                    recipesForType.put(recipeName, rcp);
                }
            }
            // Create a new FactoryType using the updated constructor that takes a map.
            // This change supports the updated design where a type maintains a mapping from product names to recipes.
            FactoryType factoryType = new FactoryType(typeName, recipesForType);
            // Store the created type in the typeMap.
            typeMap.put(typeName, factoryType);
        }
        return true;
    }

    private boolean parseBuildings(JsonNode buildingsNode) {
        // Ensure that the "buildings" field is an array.
        if (!buildingsNode.isArray()) {
            System.err.println("'buildings' field must be an array.");
            return false;
        }
        // Iterate over each building definition.
        for (JsonNode buildingNode : buildingsNode) {
            // Every building must have a "name" field.

            if (!buildingNode.has("name")) {
                System.err.println("A building is missing the 'name' field.");
                return false;
            }
            String buildingName = buildingNode.get("name").asText();
            // Create an empty list for sources; these will be populated later.
            List<Building> sources = new ArrayList<>();
            Building building = null;

            if (buildingNode.has("sources")) {
                for (JsonNode src : buildingNode.get("sources")) {
                    String srcName = src.asText();
                    sources.add(buildingMap.get(srcName));
                }
            }
            // If the building has a "type" field, it is a factory.
            if (buildingNode.has("type")) {
                String typeName = buildingNode.get("type").asText();
                // Retrieve the corresponding FactoryType from typeMap.
                FactoryType ft = typeMap.get(typeName);
                building = new Factory(buildingName, ft, sources, null, null);
            }
            // Otherwise, if it has a "mine" field, it is a mine.
            else if (buildingNode.has("mine")) {
                String mineOutput = buildingNode.get("mine").asText();
                // Create a dummy FactoryType for mines using the new constructor.
                // Since mines produce a raw resource, we pass an empty map.
                FactoryType dummyType = new FactoryType(mineOutput, new HashMap<>());
                building = new Mine(buildingName, dummyType, sources, null, null);
            } else {
                System.err.println("Building '" + buildingName + "' must have either 'type' or 'mine' field.");
                return false;
            }
            // Store the constructed building in the buildingMap.
            buildingMap.put(buildingName, building);
        }
        return true;
    }

    // TODO: Merge this to parseBuildings
    private boolean assignBuildingSources(JsonNode buildingsNode) {
        // Assign the sources for each building.
        for (JsonNode buildingNode : buildingsNode) {

        }
        return true;
    }
}
