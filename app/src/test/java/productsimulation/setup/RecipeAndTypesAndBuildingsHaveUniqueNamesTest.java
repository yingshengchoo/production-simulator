package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class RecipeAndTypesAndBuildingsHaveUniqueNamesTest {

    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * Test that a valid JSON input with unique names passes the UniqueNamesChecker.
     */
    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"Type1\", \"recipes\": [\"door\"]},"
                + "  {\"name\": \"Type2\", \"recipes\": [\"wood\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"B1\"},"
                + "  {\"name\": \"B2\"}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        RecipeAndTypesAndBuildingsHaveUniqueNames checker = new RecipeAndTypesAndBuildingsHaveUniqueNames(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when all names are unique.");
    }

    /**
     * Test that a JSON input with duplicate recipe names fails validation.
     */
    @Test
    public void test_checkMyRule_invalid_recipe() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12},"
                + "  {\"output\": \"door\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": []"
                + "}";
        JsonNode root = parseJson(json);
        RecipeAndTypesAndBuildingsHaveUniqueNames checker = new RecipeAndTypesAndBuildingsHaveUniqueNames(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when duplicate recipe names exist.");
        assertEquals("Duplicate recipe name: door", result);
    }

    /**
     * Test that a JSON input with duplicate type names fails validation.
     */
    @Test
    public void test_checkMyRule_invalid_type() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"Type1\", \"recipes\": [\"door\"]},"
                + "  {\"name\": \"Type1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": []"
                + "}";
        JsonNode root = parseJson(json);
        RecipeAndTypesAndBuildingsHaveUniqueNames checker = new RecipeAndTypesAndBuildingsHaveUniqueNames(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when duplicate type names exist.");
        assertEquals("Duplicate type name: Type1", result);
    }

    /**
     * Test that a JSON input with duplicate building names fails validation.
     */
    @Test
    public void test_checkMyRule_invalid_building() throws Exception {
        String json = "{"
                + "\"recipes\": [],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "  {\"name\": \"B1\"},"
                + "  {\"name\": \"B1\"}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        RecipeAndTypesAndBuildingsHaveUniqueNames checker = new RecipeAndTypesAndBuildingsHaveUniqueNames(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when duplicate building names exist.");
        assertEquals("Duplicate building name: B1", result);
    }
}
