package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeLatencyCheckerTest {

    /**
     * Helper method to convert a JSON string into a JsonNode.
     *
     * @param json the JSON string.
     * @return the root JsonNode.
     * @throws Exception if parsing fails.
     */
    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * Test that a valid JSON input (with all recipes having latency >= 1) passes validation.
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
        RecipeLatencyChecker checker = new RecipeLatencyChecker(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when all recipe latencies are valid (>= 1).");
    }

    /**
     * Test that an invalid JSON input (with a recipe having latency less than 1) fails validation.
     */
    @Test
    public void test_checkMyRule_invalid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":0},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": []"
                + "}";
        JsonNode root = parseJson(json);
        RecipeLatencyChecker checker = new RecipeLatencyChecker(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a recipe has latency less than 1.");
        assertEquals("Recipe 'door' has invalid latency: 0", result);
    }
}
