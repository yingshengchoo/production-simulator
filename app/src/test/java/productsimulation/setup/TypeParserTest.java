package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productsimulation.model.BuildingType;
import productsimulation.model.Recipe;
import productsimulation.model.StorageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TypeParserTest {

    // A simple ObjectMapper can be used if needed.
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe(10,
                java.util.Map.of("wood", 1, "handle", 1, "hinge", 3), "door"));
        Recipe.setRecipeGlobalList(recipes);
    }

    @AfterEach
    public void tearDown() {
        Recipe.recipeGlobalList.clear();
    }

    // Utility method to get a BufferedReader over a String.
    private BufferedReader getReader(String json) {
        return new BufferedReader(new StringReader(json));
    }

    @Test
    public void testMissingTypesField() {
        String json = "{}";
        TypeParser parser = new TypeParser();
        String error = parser.parse(getReader(json));
        assertEquals("Error: 'types' field is missing or is not an array.", error);
    }

    @Test
    public void testTypesFieldNotArray() {
        String json = "{ \"types\": { \"name\": \"Fake\" } }";
        TypeParser parser = new TypeParser();
        String error = parser.parse(getReader(json));
        assertEquals("Error: 'types' field is missing or is not an array.", error);
    }

    @Test
    public void testStorageTypeMissingRequiredFields() {
        // Missing "priority" field from "info"
        String json = "{ \"types\": [ " +
                "  { \"name\": \"Bolt Storage (100)\", \"type\": \"storage\", " +
                "    \"info\": { \"stores\": \"bolt\", \"capacity\": 100 } }" +
                "] }";
        TypeParser parser = new TypeParser();
        String error = parser.parse(getReader(json));
        assertNotNull(error, "Expected an error message because required fields are missing.");
        assertTrue(error.contains("Error: Storage type 'Bolt Storage (100)' must have 'stores', 'capacity', and 'priority'"),
                "Expected error message mentioning missing 'priority' for storage type.");
    }

    @Test
    public void testFactoryTypeMissingRecipesArray() {
        // Factory type missing the recipes array in the "info" object
        String json = "{ \"types\": [ " +
                "  { \"name\": \"Door Factory\", \"type\": \"factory\", \"info\": {} }" +
                "] }";
        TypeParser parser = new TypeParser();
        String error = parser.parse(getReader(json));
        assertNotNull(error, "Expected an error message because 'recipes' array is missing.");
        assertTrue(error.contains("Error: Factory type 'Door Factory' must have a 'recipes' array"),
                "Error message should indicate that the 'recipes' array is required.");
    }

    @Test
    public void testFactoryTypeUnknownRecipe() {
        // Factory references an unknown recipe.
        String json = "{ \"types\": [ " +
                "  { \"name\": \"Door Factory\", \"type\": \"factory\", " +
                "    \"info\": { \"recipes\": [\"bolt\"] } }" +
                "] }";
        TypeParser parser = new TypeParser();
        String error = parser.parse(getReader(json));
        assertNotNull(error, "Expected an error message because the factory references an unknown recipe.");
        assertTrue(error.contains("Error: Factory type 'Door Factory' references an unknown recipe 'bolt'"),
                "Expected error message about unknown recipe 'bolt'.");
    }

    @Test
    public void testValidStorageType() {
        String json = "{ \"types\": [ " +
                "  { \"name\": \"Bolt Storage (100)\", \"type\": \"storage\", " +
                "    \"info\": { \"stores\": \"bolt\", \"capacity\": 100, \"priority\": 1.7 } }" +
                "] }";
        TypeParser parser = new TypeParser();
        String error = parser.parse(getReader(json));
        assertNull(error, "Expected no error for a valid storage type.");
        List<BuildingType> types = parser.getTypeMap();
        assertEquals(1, types.size(), "There should be exactly one building type parsed.");
        BuildingType bt = types.get(0);
        assertTrue(bt instanceof StorageType, "The parsed building type should be an instance of StorageType.");
    }

    @Test
    public void testValidFactoryType() {
        String json = "{ \"types\": [ " +
                "  { \"name\": \"Door Factory\", \"type\": \"factory\", " +
                "    \"info\": { \"recipes\": [\"door\"] } }" +
                "] }";
        TypeParser parser = new TypeParser();
        String error = parser.parse(getReader(json));
        assertNull(error, "Expected no error for a valid factory type.");
        List<BuildingType> types = parser.getTypeMap();
        assertEquals(1, types.size(), "There should be exactly one building type parsed.");
        BuildingType bt = types.get(0);
        assertFalse(bt instanceof StorageType, "The building type should not be a StorageType for a factory.");
    }

    /**
     * Test case for an unknown building type category.
     * Expects the parser to return an error message indicating an unknown category.
     */
    @Test
    public void testUnknownCategory() {
        String json = "{\n" +
                "  \"types\": [\n" +
                "    { \"name\": \"InvalidType\", \"type\": \"invalid\", \"info\": {} }\n" +
                "  ]\n" +
                "}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        TypeParser parser = new TypeParser();
        String error = parser.parse(reader);
        assertEquals("Error: Unknown building type category 'invalid' for type 'InvalidType'.", error);
    }

    /**
     * Test case for a factory type missing the required 'recipes' array.
     * Expects an error message about the missing recipes array.
     */
    @Test
    public void testFactoryMissingRecipes() {
        String json = "{\n" +
                "  \"types\": [\n" +
                "    { \"name\": \"Door Factory\", \"type\": \"factory\", \"info\": { \"something\": \"value\" } }\n" +
                "  ]\n" +
                "}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        TypeParser parser = new TypeParser();
        String error = parser.parse(reader);
        assertEquals("Error: Factory type 'Door Factory' must have a 'recipes' array in its 'info' field.", error);
    }

    /**
     * Custom BufferedReader that simulates an IOException.
     */
    private static class FailingReader extends BufferedReader {
        public FailingReader() {
            super(new StringReader("dummy"));
        }

        @Override
        public String readLine() throws IOException {
            throw new IOException("Forced failure.");
        }
    }

    /**
     * Test case for forcing an IOException during JSON parsing.
     * Expects an error message with the IOException message.
     */
    @Test
    public void testIOException() {
        BufferedReader reader = new FailingReader();
        TypeParser parser = new TypeParser();
        String error = parser.parse(reader);
        assertNotNull( error);
    }

    /**
     * Test case for a factory type that references an unknown recipe.
     * Expects an error message indicating that the factory references a recipe that does not exist.
     */
    @Test
    public void testUnknownRecipeReference() {
        String json = "{\n" +
                "  \"types\": [\n" +
                "    { \n" +
                "      \"name\": \"Some Factory\", \n" +
                "      \"type\": \"factory\", \n" +
                "      \"info\": { \"recipes\": [ \"nonExistentRecipe\" ] } \n" +
                "    }\n" +
                "  ]\n" +
                "}";
        BufferedReader reader = new BufferedReader(new StringReader(json));
        TypeParser parser = new TypeParser();
        String error = parser.parse(reader);
        assertEquals("Error: Factory type 'Some Factory' references an unknown recipe 'nonExistentRecipe'.", error);
    }
}