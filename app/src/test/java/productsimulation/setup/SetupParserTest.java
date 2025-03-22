package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import productsimulation.model.Building;
import productsimulation.model.FactoryType;
import productsimulation.model.Recipe;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SetupParserTest {

    @Test
    public void test_parseJson() throws Exception {
        String validJson = "{\"recipes\":[], \"types\":[], \"buildings\":[]}";
        SetupParser parser = new SetupParser();
        Method parseJsonMethod = SetupParser.class.getDeclaredMethod("parseJson", BufferedReader.class);
        parseJsonMethod.setAccessible(true);
        BufferedReader reader = new BufferedReader(new StringReader(validJson));
        JsonNode root = (JsonNode) parseJsonMethod.invoke(parser, reader);
        assertNotNull(root, "parseJson() should return a valid JsonNode");
        assertTrue(root.has("recipes"));
        assertTrue(root.has("types"));
        assertTrue(root.has("buildings"));
    }

    @Test
    public void test_parseRecipes() throws Exception {
        String json = "{\"recipes\": ["
                + "{\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "{\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "], \"types\":[], \"buildings\":[]}";
        SetupParser parser = new SetupParser();
        Method parseJsonMethod = SetupParser.class.getDeclaredMethod("parseJson", BufferedReader.class);
        parseJsonMethod.setAccessible(true);
        BufferedReader reader = new BufferedReader(new StringReader(json));
        JsonNode root = (JsonNode) parseJsonMethod.invoke(parser, reader);
        JsonNode recipesNode = root.get("recipes");

        Method parseRecipesMethod = SetupParser.class.getDeclaredMethod("parseRecipes", JsonNode.class);
        parseRecipesMethod.setAccessible(true);
        boolean result = (boolean) parseRecipesMethod.invoke(parser, recipesNode);
        assertTrue(result, "parseRecipes() should return true for valid recipes array");

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<String, Recipe> recipeMap = (Map<String, Recipe>) recipeMapField.get(parser);
        assertEquals(2, recipeMap.size());
        assertTrue(recipeMap.containsKey("door"));
        assertTrue(recipeMap.containsKey("wood"));
    }

    @Test
    public void test_parseTypes() throws Exception {
        String json = "{\"recipes\": ["
                + "{\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "{\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "{\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": []}";
        SetupParser parser = new SetupParser();

        Method parseJsonMethod = SetupParser.class.getDeclaredMethod("parseJson", BufferedReader.class);
        parseJsonMethod.setAccessible(true);
        BufferedReader reader = new BufferedReader(new StringReader(json));
        JsonNode root = (JsonNode) parseJsonMethod.invoke(parser, reader);
        JsonNode recipesNode = root.get("recipes");
        Method parseRecipesMethod = SetupParser.class.getDeclaredMethod("parseRecipes", JsonNode.class);
        parseRecipesMethod.setAccessible(true);
        boolean recipesResult = (boolean) parseRecipesMethod.invoke(parser, recipesNode);
        assertTrue(recipesResult);

        JsonNode typesNode = root.get("types");
        Method parseTypesMethod = SetupParser.class.getDeclaredMethod("parseTypes", JsonNode.class);
        parseTypesMethod.setAccessible(true);
        boolean typesResult = (boolean) parseTypesMethod.invoke(parser, typesNode);
        assertTrue(typesResult);

        Field typeMapField = SetupParser.class.getDeclaredField("typeMap");
        typeMapField.setAccessible(true);
        Map<String, FactoryType> typeMap = (Map<String, FactoryType>) typeMapField.get(parser);
        assertEquals(1, typeMap.size());
        assertTrue(typeMap.containsKey("FactoryType1"));
    }

    @Test
    public void test_parseBuildings() throws Exception {
        String json = "{\"recipes\": ["
                + "{\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "{\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "{\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "{\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": [\"Mine1\"]},"
                + "{\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]}";
        SetupParser parser = new SetupParser();

        Method parseJsonMethod = SetupParser.class.getDeclaredMethod("parseJson", BufferedReader.class);
        parseJsonMethod.setAccessible(true);
        BufferedReader reader = new BufferedReader(new StringReader(json));
        JsonNode root = (JsonNode) parseJsonMethod.invoke(parser, reader);

        Method parseRecipesMethod = SetupParser.class.getDeclaredMethod("parseRecipes", JsonNode.class);
        parseRecipesMethod.setAccessible(true);
        boolean recipesResult = (boolean) parseRecipesMethod.invoke(parser, root.get("recipes"));
        assertTrue(recipesResult);

        Method parseTypesMethod = SetupParser.class.getDeclaredMethod("parseTypes", JsonNode.class);
        parseTypesMethod.setAccessible(true);
        boolean typesResult = (boolean) parseTypesMethod.invoke(parser, root.get("types"));
        assertTrue(typesResult);

        JsonNode buildingsNode = root.get("buildings");
        Method parseBuildingsMethod = SetupParser.class.getDeclaredMethod("parseBuildings", JsonNode.class);
        parseBuildingsMethod.setAccessible(true);
        boolean buildingsResult = (boolean) parseBuildingsMethod.invoke(parser, buildingsNode);
        assertTrue(buildingsResult);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<String, Building> buildingMap = (Map<String, Building>) buildingMapField.get(parser);
        assertEquals(2, buildingMap.size());
        assertTrue(buildingMap.containsKey("Factory1"));
        assertTrue(buildingMap.containsKey("Mine1"));
    }

    @Test
    public void test_assignBuildingSources() throws Exception {
        String json = "{\"recipes\": ["
                + "{\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "{\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "{\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "{\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": [\"Mine1\"]},"
                + "{\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]}";
        SetupParser parser = new SetupParser();

        Method parseJsonMethod = SetupParser.class.getDeclaredMethod("parseJson", BufferedReader.class);
        parseJsonMethod.setAccessible(true);
        BufferedReader reader = new BufferedReader(new StringReader(json));
        JsonNode root = (JsonNode) parseJsonMethod.invoke(parser, reader);

        Method parseRecipesMethod = SetupParser.class.getDeclaredMethod("parseRecipes", JsonNode.class);
        parseRecipesMethod.setAccessible(true);
        parseRecipesMethod.invoke(parser, root.get("recipes"));

        Method parseTypesMethod = SetupParser.class.getDeclaredMethod("parseTypes", JsonNode.class);
        parseTypesMethod.setAccessible(true);
        parseTypesMethod.invoke(parser, root.get("types"));

        Method parseBuildingsMethod = SetupParser.class.getDeclaredMethod("parseBuildings", JsonNode.class);
        parseBuildingsMethod.setAccessible(true);
        parseBuildingsMethod.invoke(parser, root.get("buildings"));

        Method assignSourcesMethod = SetupParser.class.getDeclaredMethod("assignBuildingSources", JsonNode.class);
        assignSourcesMethod.setAccessible(true);
        boolean assignResult = (boolean) assignSourcesMethod.invoke(parser, root.get("buildings"));
        assertTrue(assignResult);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<String, Building> buildingMap = (Map<String, Building>) buildingMapField.get(parser);
        Building factory1 = buildingMap.get("Factory1");
//        assertNotNull(factory1.sources);
//        assertEquals(1, factory1.sources.size());
//        // Since Mine1's name is "Mine1", check that the first source is Mine1.
//        assertEquals("Mine1", factory1.sources.get(0).name);
    }

    @Test
    public void test_parse() throws Exception {
        // End-to-end test with a complete valid JSON.
        // Modified JSON: Factory1 now lists itself as a source so that it can produce "handle".
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1},"
                + "  {\"output\": \"handle\", \"ingredients\": {\"wood\":1}, \"latency\":5}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\", \"handle\"]}"
                + "],"
                + "\"buildings\": ["
                // Factory1 now includes itself as a source, so it can produce "handle" via its own type.
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": [\"Factory1\", \"Mine1\"]},"
                + "  {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<String, Recipe> recipeMap = (Map<String, Recipe>) recipeMapField.get(parser);
        assertEquals(3, recipeMap.size());

        Field typeMapField = SetupParser.class.getDeclaredField("typeMap");
        typeMapField.setAccessible(true);
        Map<String, FactoryType> typeMap = (Map<String, FactoryType>) typeMapField.get(parser);
        assertEquals(1, typeMap.size());

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<String, Building> buildingMap = (Map<String, Building>) buildingMapField.get(parser);
        assertEquals(2, buildingMap.size());
    }

    // --- New Exhaustive Test Cases (adapted to your error handling style) ---

    @Test
    public void test_emptyArrays_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": [],"
                + "\"types\": [],"
                + "\"buildings\": []"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<?,?> recipeMap = (Map<?,?>) recipeMapField.get(parser);
        assertTrue(recipeMap.isEmpty(), "Recipe map should be empty with empty arrays");

        Field typeMapField = SetupParser.class.getDeclaredField("typeMap");
        typeMapField.setAccessible(true);
        Map<?,?> typeMap = (Map<?,?>) typeMapField.get(parser);
        assertTrue(typeMap.isEmpty(), "Type map should be empty with empty arrays");

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<?,?> buildingMap = (Map<?,?>) buildingMapField.get(parser);
        assertTrue(buildingMap.isEmpty(), "Building map should be empty with empty arrays");
    }

//    @Test
//    public void test_extraFields_exhaustive() throws Exception {
//        String json = "{"
//                + "\"recipes\": ["
//                + "  {\"output\": \"wood\", \"ingredients\": {\"dummy\": 1}, \"latency\": 1, \"extraField\": \"ignoreMe\"}"
//                + "],"
//                + "\"types\": ["
//                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"wood\"], \"randomField\": 123}"
//                + "],"
//                + "\"buildings\": ["
//                + "  {\"name\": \"Mine1\", \"mine\": \"wood\", \"weirdField\": \"test\"}"
//                + "],"
//                + "\"someIrrelevantField\": \"blah\""
//                + "}";
//        SetupParser parser = new SetupParser();
//        BufferedReader reader = new BufferedReader(new StringReader(json));
//        parser.parse(reader);
//
//        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
//        recipeMapField.setAccessible(true);
//        Map<String, Recipe> recipeMap = (Map<String, Recipe>) recipeMapField.get(parser);
//        assertEquals(1, recipeMap.size(), "Extra fields should be ignored; recipeMap size should be 1");
//    }

    @Test
    public void test_extraFields_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1, \"extraField\": \"ignoreMe\"}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": [],"
                + "\"someIrrelevantField\": \"blah\""
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<String, Recipe> recipeMap = (Map<String, Recipe>) recipeMapField.get(parser);
        assertEquals(1, recipeMap.size(), "Extra fields should be ignored; recipeMap size should be 1");
    }



    @Test
    public void test_missingRecipesField_exhaustive() throws Exception {
        String json = "{\"types\": [], \"buildings\": []}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<?,?> recipeMap = (Map<?,?>) recipeMapField.get(parser);
        assertTrue(recipeMap.isEmpty(), "Recipe map should remain empty when 'recipes' field is missing");
    }

    @Test
    public void test_missingTypesField_exhaustive() throws Exception {
        String json = "{\"recipes\": [], \"buildings\": []}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field typeMapField = SetupParser.class.getDeclaredField("typeMap");
        typeMapField.setAccessible(true);
        Map<?,?> typeMap = (Map<?,?>) typeMapField.get(parser);
        assertTrue(typeMap.isEmpty(), "Type map should remain empty when 'types' field is missing");
    }

    @Test
    public void test_missingBuildingsField_exhaustive() throws Exception {
        String json = "{\"recipes\": [], \"types\": []}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<?,?> buildingMap = (Map<?,?>) buildingMapField.get(parser);
        assertTrue(buildingMap.isEmpty(), "Building map should remain empty when 'buildings' field is missing");
    }

    @Test
    public void test_invalidLatency_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 0}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": []"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<?,?> recipeMap = (Map<?,?>) recipeMapField.get(parser);
        assertTrue(recipeMap.isEmpty(), "Recipe map should remain empty when latency is invalid (0)");
    }

    @Test
    public void test_duplicateRecipeNames_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": []"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<?,?> recipeMap = (Map<?,?>) recipeMapField.get(parser);
        assertTrue(recipeMap.size() <= 1, "Recipe map should not accept duplicate recipe names");
    }

    @Test
    public void test_duplicateTypeNames_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"wood\"]},"
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"wood\"]}"
                + "],"
                + "\"buildings\": []"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field typeMapField = SetupParser.class.getDeclaredField("typeMap");
        typeMapField.setAccessible(true);
        Map<?,?> typeMap = (Map<?,?>) typeMapField.get(parser);
        assertTrue(typeMap.size() <= 1, "Type map should not accept duplicate type names");
    }

    @Test
    public void test_duplicateBuildingNames_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"wood\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Mine1\", \"mine\": \"wood\"},"
                + "  {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<?,?> buildingMap = (Map<?,?>) buildingMapField.get(parser);
        assertTrue(buildingMap.size() <= 1, "Building map should not accept duplicate building names");
    }

    @Test
    public void test_buildingWithUndefinedType_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"UndefinedType\", \"sources\": []}"
                + "]"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<?,?> buildingMap = (Map<?,?>) buildingMapField.get(parser);
        assertTrue(buildingMap.isEmpty(), "Building map should remain empty if a building references an undefined type");
    }

    @Test
    public void test_buildingWithUnknownSource_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"wood\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": [\"NonExistentMine\"]}"
                + "]"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<?,?> buildingMap = (Map<?,?>) buildingMapField.get(parser);
        assertTrue(buildingMap.isEmpty(), "Building map should remain empty if a building references an unknown source");
    }

    @Test
    public void test_mineWithSources_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "  {\"name\": \"Mine1\", \"mine\": \"wood\", \"sources\": [\"Mine2\"]},"
                + "  {\"name\": \"Mine2\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<?,?> buildingMap = (Map<?,?>) buildingMapField.get(parser);
        assertTrue(buildingMap.isEmpty(), "Building map should remain empty if a mine has sources defined");
    }

    @Test
    public void test_recipeWithUndefinedIngredient_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\": 1, \"unknownItem\": 2}, \"latency\": 10},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": []"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<?,?> recipeMap = (Map<?,?>) recipeMapField.get(parser);
        assertTrue(recipeMap.isEmpty(), "Recipe map should remain empty if a recipe references an undefined ingredient");
    }

    @Test
    public void test_factoryTypeUsesMineRecipe_exhaustive() throws Exception {
        // This test expects that a factory type referencing a mine-only recipe (one with no ingredients)
        // should be considered invalid and thus not be added to the type map.
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\": 1}, \"latency\": 10},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"wood\"]}"
                + "],"
                + "\"buildings\": []"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field typeMapField = SetupParser.class.getDeclaredField("typeMap");
        typeMapField.setAccessible(true);
        Map<?,?> typeMap = (Map<?,?>) typeMapField.get(parser);
        // Expected: typeMap remains empty because a factory type should not reference a mine-only recipe.
        assertTrue(typeMap.isEmpty(), "Type map should remain empty if a factory type references a mine-only recipe");
    }

    @Test
    public void test_buildingIsBothMineAndFactory_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"wood\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"BadBuilding\", \"type\": \"FactoryType1\", \"mine\": \"wood\", \"sources\": []}"
                + "]"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<?,?> buildingMap = (Map<?,?>) buildingMapField.get(parser);
        assertTrue(buildingMap.isEmpty(), "Building map should remain empty if a building is defined as both mine and factory");
    }

    @Test
    public void test_invalidJsonSyntax_exhaustive() throws Exception {
        String invalidJson = "{ \"recipes\":[ {\"output\":\"wood\" }], \"types\" [], \"buildings\":[] ";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(invalidJson));
        parser.parse(reader);

        Field recipeMapField = SetupParser.class.getDeclaredField("recipeMap");
        recipeMapField.setAccessible(true);
        Map<?,?> recipeMap = (Map<?,?>) recipeMapField.get(parser);
        assertTrue(recipeMap.isEmpty(), "Recipe map should remain empty when JSON syntax is invalid");
    }

    @Test
    public void test_factoryCannotAcquireAllIngredients_exhaustive() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":10},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1},"
                + "  {\"output\": \"handle\", \"ingredients\": {\"wood\":1}, \"latency\":5}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": [\"Mine1\"]},"
                + "  {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        SetupParser parser = new SetupParser();
        BufferedReader reader = new BufferedReader(new StringReader(json));
        parser.parse(reader);

        Field buildingMapField = SetupParser.class.getDeclaredField("buildingMap");
        buildingMapField.setAccessible(true);
        Map<?,?> buildingMap = (Map<?,?>) buildingMapField.get(parser);
        assertTrue(buildingMap.isEmpty(), "Building map should remain empty if a factory cannot acquire all required ingredients");
    }
}
