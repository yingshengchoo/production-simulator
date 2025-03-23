package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Check rule 8
 class MinesRecipeHasEmptyIngredientsTest {

    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }


    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "    {\"output\": \"wood\", \"ingredients\": {}, \"latency\": 1},"
                + "    {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\": 12}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "    {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        MinesRecipeHasEmptyIngredients checker = new MinesRecipeHasEmptyIngredients(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error for valid mine recipe (no ingredients).");
    }


    @Test
    public void test_checkMyRule_invalid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "    {\"output\": \"wood\", \"ingredients\": {\"foo\":1}, \"latency\": 1},"
                + "    {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\": 12}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "    {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        MinesRecipeHasEmptyIngredients checker = new MinesRecipeHasEmptyIngredients(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error because the mine recipe 'wood' has ingredients.");
        assertEquals("Mine building 'Mine1' references recipe 'wood' which should have no ingredients.", result);
    }
}
