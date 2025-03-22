package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Check rule 8
public class MineRecipeCheckerTest {

    /**
     * Helper method to parse a JSON string into a JsonNode.
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
     * Test that a valid input passes the MineRecipeChecker.
     * In this test, the mine building "Mine1" references the recipe "wood", which has an empty ingredients object.
     */
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
        MineRecipeChecker checker = new MineRecipeChecker(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error for valid mine recipe (no ingredients).");
    }

    /**
     * Test that an invalid input fails the MineRecipeChecker.
     * In this test, the mine building "Mine1" references the recipe "wood",
     * but the "wood" recipe erroneously has an ingredient.
     */
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
        MineRecipeChecker checker = new MineRecipeChecker(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error because the mine recipe 'wood' has ingredients.");
        assertEquals("Mine building 'Mine1' references recipe 'wood' which should have no ingredients.", result);
    }
}
