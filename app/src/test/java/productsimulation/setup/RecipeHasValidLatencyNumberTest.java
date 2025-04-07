package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import productsimulation.setup.json_rules.RecipeHasValidLatencyNumber;

import static org.junit.jupiter.api.Assertions.*;

 class RecipeHasValidLatencyNumberTest {
     
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
        RecipeHasValidLatencyNumber checker = new RecipeHasValidLatencyNumber(null);
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
        RecipeHasValidLatencyNumber checker = new RecipeHasValidLatencyNumber(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a recipe has latency less than 1.");
        assertEquals("Recipe 'door' has invalid latency: 0", result);
    }

     // 2147483648 is one more than Integer.MAX_VALUE, so it cannot be converted to int.
     @Test
     public void test_checkMyRule_invalid_nonConvertible() throws Exception {
         String json = "{"
                 + "\"recipes\": ["
                 + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":2147483648},"
                 + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                 + "],"
                 + "\"types\": [],"
                 + "\"buildings\": []"
                 + "}";
         JsonNode root = parseJson(json);
         RecipeHasValidLatencyNumber checker = new RecipeHasValidLatencyNumber(null);
         String result = checker.checkInput(root);
         assertNotNull(result, "Expected an error when a recipe's latency value is out of int range.");
         assertEquals("Recipe 'door' has invalid latency (out of int range): 2147483648", result);
     }

 }
