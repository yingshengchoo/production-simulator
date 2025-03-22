package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import productsimulation.*;
import productsimulation.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class SetupParser {
    InputRuleChecker inputRuleChecker;

    private final Map<String, Recipe> recipeMap;
    private final Map<String, FactoryType> typeMap;
    private final Map<String, Building> buildingMap;

    public SetupParser() {
        recipeMap = new HashMap<>();
        typeMap = new HashMap<>();
        buildingMap = new HashMap<>();

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
        if (!typesNode.isArray()) {
            System.err.println("'types' field must be an array.");
            return false;
        }

        for (JsonNode typeNode : typesNode) {
            if (!typeNode.has("name") || !typeNode.has("recipes")) {
                System.err.println("A type is missing required fields (name, recipes).");
                return false;
            }
            String typeName = typeNode.get("name").asText();
            Map<String, Recipe> recipesForType = new HashMap<>();
            for (JsonNode recName : typeNode.get("recipes")) {
                String recipeName = recName.asText();
                Recipe rcp = recipeMap.get(recipeName);
                if (rcp != null) {
                    recipesForType.put(recipeName, rcp);
                }
            }
            FactoryType factoryType = new FactoryType(typeName, recipesForType);
            typeMap.put(typeName, factoryType);
        }
        return true;
    }

    private boolean parseBuildings(JsonNode buildingsNode) {
        if (!buildingsNode.isArray()) {
            System.err.println("'buildings' field must be an array.");
            return false;
        }
        for (JsonNode buildingNode : buildingsNode) {
            if (!buildingNode.has("name")) {
                System.err.println("A building is missing the 'name' field.");
                return false;
            }
            String buildingName = buildingNode.get("name").asText();
            Building building = null;

            if (buildingNode.has("type")) {
                String typeName = buildingNode.get("type").asText();
                FactoryType ft = typeMap.get(typeName);
                building = new Factory(buildingName, ft, null, null);
            }
            else if (buildingNode.has("mine")) {
                String mineOutput = buildingNode.get("mine").asText();
                FactoryType dummyType = new FactoryType(mineOutput, new HashMap<>());
                building = new Mine(buildingName, dummyType, null, null);
            } else {
                System.err.println("Building '" + buildingName + "' must have either 'type' or 'mine' field.");
                return false;
            }
            buildingMap.put(buildingName, building);
        }

        for (JsonNode buildingNode : buildingsNode) {
            String buildingName = buildingNode.get("name").asText();
            List<Building> sources = new ArrayList<>();
            if (buildingNode.has("sources")) {
                for (JsonNode src : buildingNode.get("sources")) {
                    String srcName = src.asText();
                    sources.add(buildingMap.get(srcName));
                }
            }
            buildingMap.get(buildingName).setSources(sources);
        }
        return true;
    }
}
