package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class RecipesIngredientsMustBeWellDefinedTest {

    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * Test that a valid JSON input (where each recipe's ingredients are defined) passes validation.
     */
    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": []"
                + "}";
        JsonNode root = parseJson(json);
        RecipesIngredientsMustBeWellDefined checker = new RecipesIngredientsMustBeWellDefined(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when all ingredients are defined.");
    }

    /**
     * Test that an invalid JSON input (where a recipe references an undefined ingredient) fails validation.
     * For example, the "door" recipe requires "handle" which is not defined as any recipe output.
     */
    @Test
    public void test_checkMyRule_invalid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": []"
                + "}";
        JsonNode root = parseJson(json);
        RecipesIngredientsMustBeWellDefined checker = new RecipesIngredientsMustBeWellDefined(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a recipe references an undefined ingredient.");
        assertEquals("Recipe 'door' requires ingredient 'handle', which is not defined.", result);
    }
}
