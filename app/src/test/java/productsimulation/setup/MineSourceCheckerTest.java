package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class MineSourceCheckerTest {

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
     * Test that a mine building with no "sources" field passes validation.
     */
    @Test
    public void test_checkMyRule_valid_noSources() throws Exception {
        String json = "{"
                + "\"recipes\": [],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "   {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        MineSourceChecker checker = new MineSourceChecker(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when mine building has no sources field.");
    }

    /**
     * Test that a mine building with an empty "sources" array passes validation.
     */
    @Test
    public void test_checkMyRule_valid_emptySources() throws Exception {
        String json = "{"
                + "\"recipes\": [],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "   {\"name\": \"Mine1\", \"mine\": \"wood\", \"sources\": []}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        MineSourceChecker checker = new MineSourceChecker(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when mine building has empty sources array.");
    }

    /**
     * Test that a mine building with a non-empty "sources" array fails validation.
     */
    @Test
    public void test_checkMyRule_invalid_nonEmptySources() throws Exception {
        String json = "{"
                + "\"recipes\": [],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "   {\"name\": \"Mine1\", \"mine\": \"wood\", \"sources\": [\"SomeSource\"]}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        MineSourceChecker checker = new MineSourceChecker(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when mine building has non-empty sources.");
        assertEquals("Mine building 'Mine1' must not have non-empty 'sources'.", result);
    }

    /**
     * Test that a non-mine building (e.g. a factory) is ignored by the MineSourceChecker,
     * even if it has non-empty "sources" because this rule only applies to mine buildings.
     */
    @Test
    public void test_checkMyRule_nonMineIgnored() throws Exception {
        String json = "{"
                + "\"recipes\": [],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "   {\"name\": \"Factory1\", \"type\": \"SomeType\", \"sources\": [\"SomeSource\"]}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        MineSourceChecker checker = new MineSourceChecker(null);
        String result = checker.checkInput(root);
        // Since the building is not a mine, no error should be reported.
        assertNull(result, "Expected no error for non-mine building even if sources is non-empty.");
    }
}
