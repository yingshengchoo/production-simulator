package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import productsimulation.Coordinate;
import productsimulation.model.*;
import productsimulation.model.drone.DronePort;
import productsimulation.setup.json_rules.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class SetupParser {
    InputRuleChecker inputRuleChecker;

    private final Map<String, Recipe> recipeMap;
    private final Map<String, BuildingType> typeMap;
    private final Map<String, Building> buildingMap;

    public SetupParser() {
        recipeMap = new LinkedHashMap<>();
        typeMap = new LinkedHashMap<>();
        buildingMap = new LinkedHashMap<>();

        inputRuleChecker = new AllRequiredFieldsArePresentAndLegal(
                new RecipeAndTypesAndBuildingsAreAllPresent(
                        new RecipeAndTypesAndBuildingsHaveUniqueNames(
                                new NameHasNoApostrophe(
                                        new BuildingsTypesAreWellDefined(
                                                new SourceBuildingAreWellDefined(
                                                        new MineHasEmptySources(
                                                                new TypesRecipeExist(
                                                                        new RecipesIngredientsMustBeWellDefined(
                                                                                new FactorysRecipeHasIngredients(
                                                                                        new MinesRecipeHasEmptyIngredients(
                                                                                                new RecipeHasValidLatencyNumber(
                                                                                                        new FactoryCanGetAllIngredientsItNeeds(
                                                                                                                new NoRepetitiveCoordinate(
                                                                                                                       new  StorageHasRequiredFields(null)
                                                                                                                )
                                                                                                        )
                                                                                                )))))))))))
        );
    }

    private JsonNode parseJson(BufferedReader reader) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(reader);
        } catch (IOException e) {
            return null;
        }
    }

    public String parse(BufferedReader r) {
        JsonNode root = parseJson(r);
        if (root == null) {
            return "Failed to parse JSON file.";
        }

        String errorMessage = inputRuleChecker.checkInput(root);
        if (errorMessage != null) {
            return "Input validation error: " + errorMessage;
        }
        parseRecipes(root.get("recipes"));
        parseTypes(root.get("types"));
        parseBuildings(root.get("buildings"));

        return null;
    }

    private void parseRecipes(JsonNode recipesNode) {
        for (JsonNode recipeNode : recipesNode) {
            String output = recipeNode.get("output").asText();
            int latency = recipeNode.get("latency").asInt();
            Map<String, Integer> ingredients = new LinkedHashMap<>();
            Iterator<String> fields = recipeNode.get("ingredients").fieldNames();
            while (fields.hasNext()) {
                String key = fields.next();
                ingredients.put(key, recipeNode.get("ingredients").get(key).asInt());
            }
            Recipe recipe = new Recipe(latency, ingredients, output);
            recipeMap.put(output, recipe);
        }
    }

    private void parseTypes(JsonNode typesNode) {
        for (JsonNode typeNode : typesNode) {
            String typeName = typeNode.get("name").asText();
            Map<String, Recipe> recipesForType = new HashMap<>();
            for (JsonNode recName : typeNode.get("recipes")) {
                String recipeName = recName.asText();
                Recipe rcp = recipeMap.get(recipeName);
                recipesForType.put(recipeName, rcp);
            }
            BuildingType factoryType = new BuildingType(typeName, recipesForType);
            typeMap.put(typeName, factoryType);
        }
    }

    private void parseBuildings(JsonNode buildingsNode) {
        // First pass: process buildings that specify coordinates.
        for (JsonNode buildingNode : buildingsNode) {
            if (buildingNode.has("x") && buildingNode.has("y")) {
                String buildingName = buildingNode.get("name").asText();
                Building building;
                int x = buildingNode.get("x").asInt();
                int y = buildingNode.get("y").asInt();
                Coordinate coord = new Coordinate(x, y);

                if (buildingNode.has("type")) {
                    String typeName = buildingNode.get("type").asText();
                    BuildingType ft = typeMap.get(typeName);

                    if ("droneport".equalsIgnoreCase(typeName)) {
                        // build a DronePort instead of a Factory
                        building = new DronePort(
                                buildingName,
                                ft,
                                null,    // source policy will be set in modelSetup
                                null,    // serve policy ditto
                                coord
                        );
                    } else {
                        building = new Factory(
                                buildingName,
                                ft,
                                null,
                                null,
                                null,
                                coord
                        );
                    }
                } else if (buildingNode.has("mine")) {
                    String mineOutput = buildingNode.get("mine").asText();
                    Map<String, Recipe> recipes = new HashMap<>();
                    recipes.put(mineOutput, recipeMap.get(mineOutput));
                    BuildingType dummyType = new BuildingType(mineOutput, recipes);
                    building = new Mine(buildingName, dummyType, null,null, null, coord);
                } else if (buildingNode.has("stores")) {
                    String storeItem = buildingNode.get("stores").asText();
                    int capacity = buildingNode.get("capacity").asInt();
                    double priority = buildingNode.get("priority").asDouble();
                    // Create a Storage building. Assume Storage is defined in productsimulation.model.
                    building = new Storage(buildingName, storeItem, capacity, priority, null, null, coord);
                } else {
                    continue;
                }

                buildingMap.put(buildingName, building);
            }
        }

        // Second pass: process buildings that do not have coordinates (legacy input).
        for (JsonNode buildingNode : buildingsNode) {
            if (!(buildingNode.has("x") && buildingNode.has("y"))) {
                String buildingName = buildingNode.get("name").asText();
                Building building;
                // For buildings without coordinates, use a constructor that auto-assigns a valid coordinate.
                if (buildingNode.has("type")) {
                    String typeName = buildingNode.get("type").asText();
                    BuildingType ft = typeMap.get(typeName);
                    building = new Factory(buildingName, ft, null, null); // This constructor should auto-assign a coordinate.
                } else if (buildingNode.has("mine")) {
                    String mineOutput = buildingNode.get("mine").asText();
                    Map<String, Recipe> recipes = new HashMap<>();
                    recipes.put(mineOutput, recipeMap.get(mineOutput));
                    BuildingType dummyType = new BuildingType(mineOutput, recipes);
                    building = new Mine(buildingName, dummyType, null, null);
                } else if (buildingNode.has("stores")) {
                    String storeItem = buildingNode.get("stores").asText();
                    int capacity = buildingNode.get("capacity").asInt();
                    double priority = buildingNode.get("priority").asDouble();
                    building = new Storage(buildingName, storeItem, null, capacity, priority, null, null);
                } else {
                    continue;
                }

                buildingMap.put(buildingName, building);
            }
        }

        // Third pass: assign sources to each building.
        for (JsonNode buildingNode : buildingsNode) {
            String buildingName = buildingNode.get("name").asText();
            List<Building> sources = new ArrayList<>();
            for (JsonNode src : buildingNode.get("sources")) {
                String srcName = src.asText();
                sources.add(buildingMap.get(srcName));
            }
            buildingMap.get(buildingName).setSources(sources);
        }
    }

    public Map<String, Recipe> getRecipeMap() {
        return Collections.unmodifiableMap(recipeMap);
    }

    public Map<String, BuildingType> getTypeMap() {
        return Collections.unmodifiableMap(typeMap);
    }

    public Map<String, Building> getBuildingMap() {
        return Collections.unmodifiableMap(buildingMap);
    }
}