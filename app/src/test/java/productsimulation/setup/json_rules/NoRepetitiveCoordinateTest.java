package productsimulation.setup.json_rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NoRepetitiveCoordinateTest {

    private final ObjectMapper mapper = new ObjectMapper();

    // Helper method to instantiate the NoRepetitiveCoordinate checker.
    private NoRepetitiveCoordinate createChecker() {
        return new NoRepetitiveCoordinate(null);
    }

    @Test
    public void testMissingBuildingsField() throws Exception {
        // If there is no "buildings" field at all, the rule should return null.
        String json = "{}";
        JsonNode root = mapper.readTree(json);
        String result = createChecker().checkMyRule(root);
        assertNull(result, "Expected null when 'buildings' field is missing.");
    }

    @Test
    public void testBuildingsFieldNotArray() throws Exception {
        // If the "buildings" field is not an array, the rule should return null.
        String json = "{ \"buildings\": \"not an array\" }";
        JsonNode root = mapper.readTree(json);
        String result = createChecker().checkMyRule(root);
        assertNull(result, "Expected null when 'buildings' field is not an array.");
    }

    @Test
    public void testEmptyBuildingsArray() throws Exception {
        // If the buildings array is empty, then there are no duplicate coordinates.
        String json = "{ \"buildings\": [] }";
        JsonNode root = mapper.readTree(json);
        String result = createChecker().checkMyRule(root);
        assertNull(result, "Expected null for an empty buildings array.");
    }

    @Test
    public void testNoDuplicateCoordinates() throws Exception {
        // Valid input with two buildings with distinct coordinates.
        String json = "{ \"buildings\": [ " +
                "{\"name\": \"Building A\", \"x\": 1, \"y\": 2}," +
                "{\"name\": \"Building B\", \"x\": 2, \"y\": 3}" +
                "] }";
        JsonNode root = mapper.readTree(json);
        String result = createChecker().checkMyRule(root);
        assertNull(result, "Expected null when no duplicate coordinates exist.");
    }

    @Test
    public void testDuplicateCoordinates() throws Exception {
        // Input with duplicate coordinates: both Building A and Building B have x=1, y=2.
        String json = "{ \"buildings\": [ " +
                "{\"name\": \"Building A\", \"x\": 1, \"y\": 2}," +
                "{\"name\": \"Building B\", \"x\": 1, \"y\": 2}" +
                "] }";
        JsonNode root = mapper.readTree(json);
        String result = createChecker().checkMyRule(root);
        assertNotNull(result, "Expected an error message when duplicate coordinates exist.");
        assertTrue(result.contains("Duplicate coordinate found:"), "Error message should mention a duplicate coordinate.");
        assertTrue(result.contains("Building B"), "Error message should contain the duplicate building name.");
    }

    @Test
    public void testIgnoreIncompleteCoordinates() throws Exception {
        // If a building is missing either x or y, it should be ignored.
        String json = "{ \"buildings\": [ " +
                "{\"name\": \"Building A\", \"x\": 1, \"y\": 2}," +
                "{\"name\": \"Building B\", \"x\": 1}" +
                "] }";
        JsonNode root = mapper.readTree(json);
        String result = createChecker().checkMyRule(root);
        assertNull(result, "Expected null when incomplete coordinate fields are ignored.");
    }

    @Test
    public void testBuildingWithMissingNameReturnsUnknown() throws Exception {
        // JSON with two buildings having the same coordinates,
        // where the second building does not have a "name" field.
        String json = "{ \"buildings\": [ " +
                "{\"name\": \"Alpha\", \"x\": 1, \"y\": 2}," +
                "{\"x\": 1, \"y\": 2}" +
                "] }";
        JsonNode root = mapper.readTree(json);

        // Call the rule checker.
        NoRepetitiveCoordinate checker = createChecker();
        String result = checker.checkMyRule(root);

        // Verify that a duplicate is detected.
        assertNotNull(result, "Expected an error message when duplicate coordinates are found.");

        // Check that the error message mentions 'unknown' since the second building has no name.
        assertTrue(result.contains("unknown"), "Error message should contain 'unknown' for a building without a name.");
    }
}