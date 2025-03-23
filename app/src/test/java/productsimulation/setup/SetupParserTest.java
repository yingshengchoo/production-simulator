package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

class SetupParserTest {

    private SetupParser parser;

    @BeforeEach
    public void setup() {
        parser = new SetupParser();
    }

    /**
     * Helper method to invoke a private method that parses JSON from a BufferedReader.
     *
     * @param json the JSON string to parse.
     * @return the resulting JsonNode.
     * @throws Exception if reflection fails.
     */
    private JsonNode invokeParseJson(String json) throws Exception {
        Method method = SetupParser.class.getDeclaredMethod("parseJson", BufferedReader.class);
        method.setAccessible(true);
        BufferedReader reader = new BufferedReader(new StringReader(json));
        return (JsonNode) method.invoke(parser, reader);
    }

    /**
     * Generic helper to invoke any private parse method (e.g., parseRecipes, parseTypes, parseBuildings).
     *
     * @param methodName the name of the method.
     * @param node       the JsonNode to pass.
     * @return the boolean result from the method.
     * @throws Exception if reflection fails.
     */
    private boolean invokeParseMethod(String methodName, JsonNode node) throws Exception {
        Method method = SetupParser.class.getDeclaredMethod(methodName, JsonNode.class);
        method.setAccessible(true);
        return (boolean) method.invoke(parser, node);
    }

    /**
     * Helper to retrieve a private field by name.
     *
     * @param fieldName the field name.
     * @param <T>       the expected type.
     * @return the field value.
     * @throws Exception if reflection fails.
     */
    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(String fieldName) throws Exception {
        Field field = SetupParser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(parser);
    }

    /**
     * Helper to invoke the public parse() method.
     *
     * @param json the JSON string to parse.
     * @throws Exception if parsing fails.
     */
    private void parseAll(String json) throws Exception {
        parser.parse(new BufferedReader(new StringReader(json)));
    }

    @Nested
    class JsonParsingTests {

        @Test
        public void test_parseJson() throws Exception {
            String validJson = "{\"recipes\":[], \"types\":[], \"buildings\":[]}";
            JsonNode root = invokeParseJson(validJson);
            assertAll("Parsed JSON",
                    () -> assertNotNull(root, "parseJson() should return a valid JsonNode"),
                    () -> assertTrue(root.has("recipes"), "JSON should contain 'recipes'"),
                    () -> assertTrue(root.has("types"), "JSON should contain 'types'"),
                    () -> assertTrue(root.has("buildings"), "JSON should contain 'buildings'")
            );
        }

        @Test
        public void test_invalidJsonSyntax() throws Exception {
            String invalidJson = "{ \"recipes\":[ {\"output\":\"wood\" }], \"types\" [], \"buildings\":[] ";
            parseAll(invalidJson);
            Map<?,?> recipeMap = getPrivateField("recipeMap");
            assertTrue(recipeMap.isEmpty(), "Recipe map should remain empty when JSON syntax is invalid");
        }
    }

    @Nested
    class RecipeParsingTests {
        @Test
        public void test_invalidLatency() throws Exception {
            String json = "{"
                    + "\"recipes\": ["
                    + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 0}"
                    + "],"
                    + "\"types\": [],"
                    + "\"buildings\": []"
                    + "}";
            parseAll(json);
            Map<?,?> recipeMap = getPrivateField("recipeMap");
            assertTrue(recipeMap.isEmpty(), "Recipe map should remain empty when latency is invalid (0)");
        }

        @Test
        public void test_duplicateRecipeNames() throws Exception {
            String json = "{"
                    + "\"recipes\": ["
                    + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1},"
                    + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                    + "],"
                    + "\"types\": [],"
                    + "\"buildings\": []"
                    + "}";
            parseAll(json);
            Map<?,?> recipeMap = getPrivateField("recipeMap");
            assertTrue(recipeMap.size() <= 1, "Recipe map should not accept duplicate recipe names");
        }

        @Test
        public void test_recipeWithUndefinedIngredient() throws Exception {
            String json = "{"
                    + "\"recipes\": ["
                    + "  {\"output\": \"door\", \"ingredients\": {\"wood\": 1, \"unknownItem\": 2}, \"latency\": 10},"
                    + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1}"
                    + "],"
                    + "\"types\": [],"
                    + "\"buildings\": []"
                    + "}";
            parseAll(json);
            Map<?,?> recipeMap = getPrivateField("recipeMap");
            assertTrue(recipeMap.isEmpty(), "Recipe map should remain empty if a recipe references an undefined ingredient");
        }
    }

    @Nested
    class OverallParsingTests {

        @Test
        public void test_parse() throws Exception {
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
                    + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": [\"Factory1\", \"Mine1\"]},"
                    + "  {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                    + "]"
                    + "}";
            parseAll(json);

            Map<String, Recipe> recipeMap = getPrivateField("recipeMap");
            Map<String, FactoryType> typeMap = getPrivateField("typeMap");
            Map<String, Building> buildingMap = getPrivateField("buildingMap");

            assertAll("Overall Parsing Checks",
                    () -> assertEquals(3, recipeMap.size(), "There should be three recipes parsed"),
                    () -> assertEquals(1, typeMap.size(), "There should be one factory type parsed"),
                    () -> assertEquals(2, buildingMap.size(), "There should be two buildings parsed")
            );
        }

        @Test
        public void test_emptyArrays() throws Exception {
            String json = "{"
                    + "\"recipes\": [],"
                    + "\"types\": [],"
                    + "\"buildings\": []"
                    + "}";
            parseAll(json);

//            assertAll("Empty Arrays Checks",
//                    () -> assertTrue(getPrivateField("recipeMap").isEmpty(), "Recipe map should be empty with empty arrays"),
//                    () -> assertTrue(getPrivateField("typeMap").isEmpty(), "Type map should be empty with empty arrays"),
//                    () -> assertTrue(getPrivateField("buildingMap").isEmpty(), "Building map should be empty with empty arrays")
//            );
        }

        @Test
        public void test_extraFields() throws Exception {
            String json = "{"
                    + "\"recipes\": ["
                    + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1, \"extraField\": \"ignoreMe\"}"
                    + "],"
                    + "\"types\": [],"
                    + "\"buildings\": [],"
                    + "\"someIrrelevantField\": \"blah\""
                    + "}";
            parseAll(json);
            Map<String, Recipe> recipeMap = getPrivateField("recipeMap");
            assertEquals(1, recipeMap.size(), "Extra fields should be ignored; recipeMap size should be 1");
        }

        @Test
        public void test_missingRecipesField() throws Exception {
            String json = "{\"types\": [], \"buildings\": []}";
            parseAll(json);
            Map<?,?> recipeMap = getPrivateField("recipeMap");
            assertTrue(recipeMap.isEmpty(), "Recipe map should remain empty when 'recipes' field is missing");
        }

        @Test
        public void test_missingTypesField() throws Exception {
            String json = "{\"recipes\": [], \"buildings\": []}";
            parseAll(json);
            Map<?,?> typeMap = getPrivateField("typeMap");
            assertTrue(typeMap.isEmpty(), "Type map should remain empty when 'types' field is missing");
        }

        @Test
        public void test_missingBuildingsField() throws Exception {
            String json = "{\"recipes\": [], \"types\": []}";
            parseAll(json);
            Map<?,?> buildingMap = getPrivateField("buildingMap");
            assertTrue(buildingMap.isEmpty(), "Building map should remain empty when 'buildings' field is missing");
        }
    }
}
