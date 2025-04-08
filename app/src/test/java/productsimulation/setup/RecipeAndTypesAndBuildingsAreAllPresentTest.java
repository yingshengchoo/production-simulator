package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import productsimulation.setup.json_rules.RecipeAndTypesAndBuildingsAreAllPresent;

import static org.junit.jupiter.api.Assertions.*;

 class RecipeAndTypesAndBuildingsAreAllPresentTest {

    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * Test that a JSON input with all required fields passes the check.
     */
    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{\"recipes\": [], \"types\": [], \"buildings\": []}";
        JsonNode root = parseJson(json);
        RecipeAndTypesAndBuildingsAreAllPresent checker = new RecipeAndTypesAndBuildingsAreAllPresent(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when 'recipes', 'types', and 'buildings' are present.");
    }

    /**
     * Test that a JSON input missing the 'recipes' field fails the check.
     */
    @Test
    public void test_checkMyRule_missingRecipes() throws Exception {
        String json = "{\"types\": [], \"buildings\": []}";
        JsonNode root = parseJson(json);
        RecipeAndTypesAndBuildingsAreAllPresent checker = new RecipeAndTypesAndBuildingsAreAllPresent(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when 'recipes' is missing.");
        assertEquals("JSON file must contain 'recipes', 'types', and 'buildings' fields.", result);
    }

    /**
     * Test that a JSON input missing the 'types' field fails the check.
     */
    @Test
    public void test_checkMyRule_missingTypes() throws Exception {
        String json = "{\"recipes\": [], \"buildings\": []}";
        JsonNode root = parseJson(json);
        RecipeAndTypesAndBuildingsAreAllPresent checker = new RecipeAndTypesAndBuildingsAreAllPresent(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when 'types' is missing.");
        assertEquals("JSON file must contain 'recipes', 'types', and 'buildings' fields.", result);
    }

    /**
     * Test that a JSON input missing the 'buildings' field fails the check.
     */
    @Test
    public void test_checkMyRule_missingBuildings() throws Exception {
        String json = "{\"recipes\": [], \"types\": []}";
        JsonNode root = parseJson(json);
        RecipeAndTypesAndBuildingsAreAllPresent checker = new RecipeAndTypesAndBuildingsAreAllPresent(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when 'buildings' is missing.");
        assertEquals("JSON file must contain 'recipes', 'types', and 'buildings' fields.", result);
    }
}
