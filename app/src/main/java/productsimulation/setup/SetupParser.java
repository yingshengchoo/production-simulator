package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                                                                                                        new FactoryCanGetAllIngredientsItNeeds(null)
                                                                                                )))))))))))
        )
                ;
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
            FactoryType factoryType = new FactoryType(typeName, recipesForType);
            typeMap.put(typeName, factoryType);
        }
    }

    private void parseBuildings(JsonNode buildingsNode) {
        for (JsonNode buildingNode : buildingsNode) {
            String buildingName = buildingNode.get("name").asText();
//            if (buildingName.startsWith("'") && buildingName.endsWith("'")) {
//                buildingName = buildingName.substring(1, buildingName.length() - 1);
//            }
            Building building ;

            if (buildingNode.has("type")) {
                String typeName = buildingNode.get("type").asText();
                FactoryType ft = typeMap.get(typeName);
                building = new Factory(buildingName, ft, null, null);
            } else  {
                // 我记得mine可以有多种output
                String mineOutput = buildingNode.get("mine").asText();
//                FactoryType dummyType = new FactoryType(mineOutput, new HashMap<>());
//                mine还是有个recipes会比较符合直觉
//                这里我写得比较粗糙，没有考虑异常。
                Map<String, Recipe> recipes = new HashMap<>();
                recipes.put(mineOutput, recipeMap.get(mineOutput));
                FactoryType dummyType = new FactoryType(mineOutput, recipes);
                building = new Mine(buildingName, dummyType, null, null);
            }
            buildingMap.put(buildingName, building);
        }

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

    public Map<String, FactoryType> getTypeMap() {
        return Collections.unmodifiableMap(typeMap);
    }

    public Map<String, Building> getBuildingMap() {
        return Collections.unmodifiableMap(buildingMap);
    }
}
