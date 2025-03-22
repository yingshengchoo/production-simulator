package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SourcesDefinedCheckerTest {

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
     * Test that a valid JSON input passes the SourcesDefinedChecker.
     * In this valid input, every source reference in a building is defined.
     */
    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{"
                + "\"recipes\": [],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "    {\"name\": \"B1\"},"
                + "    {\"name\": \"B2\", \"sources\": [\"B1\"]}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        SourcesDefinedChecker checker = new SourcesDefinedChecker(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when all source references are defined.");
    }

    /**
     * Test that an invalid JSON input fails the SourcesDefinedChecker.
     * In this case, a building references a source that is not defined.
     */
    @Test
    public void test_checkMyRule_invalid() throws Exception {
        String json = "{"
                + "\"recipes\": [],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "    {\"name\": \"B1\", \"sources\": [\"B2\"]}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        SourcesDefinedChecker checker = new SourcesDefinedChecker(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a building references an undefined source.");
        assertEquals("Building 'B1' references unknown source: B2", result);
    }
}
