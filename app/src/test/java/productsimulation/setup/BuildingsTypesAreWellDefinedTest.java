package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import productsimulation.setup.json_rules.BuildingsTypesAreWellDefined;

import static org.junit.jupiter.api.Assertions.*;

class BuildingsTypesAreWellDefinedTest {

    private JsonNode getJsonNode(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * Test that a valid JSON input passes the BuildingTypeChecker.
     * In this case, the building with a "type" field references an existing type.
     */
    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{"
                + "\"types\": [ { \"name\": \"T1\" } ],"
                + "\"buildings\": [ "
                + "  { \"name\": \"B1\", \"type\": \"T1\" },"
                + "  { \"name\": \"B2\", \"mine\": \"M1\" }"
                + "]"
                + "}";
        JsonNode root = getJsonNode(json);
        // Instantiate BuildingTypeChecker with no next rule.
        BuildingsTypesAreWellDefined checker = new BuildingsTypesAreWellDefined(null);
        String result = checker.checkInput(root);
        // A valid input should return null.
        assertNull(result, "Expected no error for valid building types.");
    }

    /**
     * Test that an invalid JSON input fails the BuildingTypeChecker.
     * In this test, a building references an unknown type.
     */
    @Test
    public void test_checkMyRule_invalid() throws Exception {
        String json = "{"
                + "\"types\": [ { \"name\": \"T1\" } ],"
                + "\"buildings\": [ "
                + "  { \"name\": \"B1\", \"type\": \"T2\" }"
                + "]"
                + "}";
        JsonNode root = getJsonNode(json);
        BuildingsTypesAreWellDefined checker = new BuildingsTypesAreWellDefined(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected error message for unknown building type.");
        assertEquals("Building 'B1' has unknown type: T2", result);
    }
}
