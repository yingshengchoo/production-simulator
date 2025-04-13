package productsimulation.setup.json_rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StorageHasRequiredFieldsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testValidStorageBuilding() throws Exception {
        // Valid storage building: all required fields exist and are legal.
        String json = "{ \"buildings\": [ { \"name\": \"Storage A\", " +
                "\"stores\": \"bolt\", " +
                "\"capacity\": 100, " +
                "\"priority\": 1.5, " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNull(error, "A valid storage building should produce no error.");
    }

//    @Test
//    public void testMissingStoresField() throws Exception {
//        // Missing "stores" field.
//        String json = "{ \"buildings\": [ { \"name\": \"Storage B\", " +
//                "\"capacity\": 100, " +
//                "\"priority\": 1.5, " +
//                "\"sources\": [] } ] }";
//        JsonNode root = mapper.readTree(json);
//        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
//        String error = checker.checkMyRule(root);
//        assertNotNull(error, "Missing 'stores' field should trigger an error.");
//        assertTrue(error.contains("must have a valid 'stores' field"));
//    }

    @Test
    public void testEmptyStoresField() throws Exception {
        // "stores" is empty.
        String json = "{ \"buildings\": [ { \"name\": \"Storage C\", " +
                "\"stores\": \"\", " +
                "\"capacity\": 100, " +
                "\"priority\": 1.5, " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Empty 'stores' field should trigger an error.");
        assertTrue(error.contains("must have a valid 'stores' field"));
    }

    @Test
    public void testMissingCapacity() throws Exception {
        // Missing "capacity" field.
        String json = "{ \"buildings\": [ { \"name\": \"Storage D\", " +
                "\"stores\": \"bolt\", " +
                "\"priority\": 1.5, " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Missing 'capacity' field should trigger an error.");
        assertTrue(error.contains("must have a 'capacity' field"));
    }

    @Test
    public void testNonIntegerCapacity() throws Exception {
        // Non-integer capacity field.
        String json = "{ \"buildings\": [ { \"name\": \"Storage E\", " +
                "\"stores\": \"bolt\", " +
                "\"capacity\": \"high\", " +
                "\"priority\": 1.5, " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Non-integer capacity should trigger an error.");
        assertTrue(error.contains("has an invalid 'capacity'"));
    }

    @Test
    public void testNegativeCapacity() throws Exception {
        // Negative capacity.
        String json = "{ \"buildings\": [ { \"name\": \"Storage F\", " +
                "\"stores\": \"bolt\", " +
                "\"capacity\": -10, " +
                "\"priority\": 1.5, " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Negative capacity should trigger an error.");
        assertTrue(error.contains("must have a positive 'capacity'"));
    }

    @Test
    public void testMissingPriority() throws Exception {
        // Missing "priority" field.
        String json = "{ \"buildings\": [ { \"name\": \"Storage G\", " +
                "\"stores\": \"bolt\", " +
                "\"capacity\": 100, " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Missing 'priority' field should trigger an error.");
        assertTrue(error.contains("must have a 'priority' field"));
    }

    @Test
    public void testNonNumericPriority() throws Exception {
        // Non-numeric "priority" field.
        String json = "{ \"buildings\": [ { \"name\": \"Storage H\", " +
                "\"stores\": \"bolt\", " +
                "\"capacity\": 100, " +
                "\"priority\": \"high\", " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Non-numeric priority should trigger an error.");
        assertTrue(error.contains("has an invalid 'priority'"));
    }

    @Test
    public void testZeroPriority() throws Exception {
        // Zero priority.
        String json = "{ \"buildings\": [ { \"name\": \"Storage I\", " +
                "\"stores\": \"bolt\", " +
                "\"capacity\": 100, " +
                "\"priority\": 0, " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Zero priority should trigger an error.");
        assertTrue(error.contains("must have a positive 'priority'"));
    }

    @Test
    public void testMissingSources() throws Exception {
        // Missing "sources" field.
        String json = "{ \"buildings\": [ { \"name\": \"Storage J\", " +
                "\"stores\": \"bolt\", " +
                "\"capacity\": 100, " +
                "\"priority\": 1.5 } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Missing 'sources' field should trigger an error.");
        assertTrue(error.contains("must have a 'sources' field"));
    }

    @Test
    public void testSourcesNotArray() throws Exception {
        // "sources" is not an array.
        String json = "{ \"buildings\": [ { \"name\": \"Storage K\", " +
                "\"stores\": \"bolt\", " +
                "\"capacity\": 100, " +
                "\"priority\": 1.5, " +
                "\"sources\": \"not_an_array\" } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkMyRule(root);
        assertNotNull(error, "Non-array sources should trigger an error.");
        assertTrue(error.contains("has a 'sources' field that is not an array"));
    }

    @Test
    public void testNoBuildingsField() throws Exception {
        // When "buildings" field is missing completely, rule should not produce an error.
        String json = "{}";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        // The rule returns null because there is nothing to check.
        String error = checker.checkInput(root);
        assertNull(error, "No 'buildings' field present should produce no error.");
    }

    @Test
    public void testMissingNameField() throws Exception {
        // Create a storage building JSON without a "name" field.
        // In this case the error message should use "unknown" for the building's name.
        // For instance, we intentionally supply an empty 'stores' value to trigger an error.
        String json = "{ \"buildings\": [ { \"stores\": \"\", " +
                "\"capacity\": 100, " +
                "\"priority\": 1.5, " +
                "\"sources\": [] } ] }";
        JsonNode root = mapper.readTree(json);
        StorageHasRequiredFields checker = new StorageHasRequiredFields(null);
        String error = checker.checkInput(root);
        assertNotNull(error, "Missing 'name' field should still lead to an error due to invalid 'stores' value.");
        // We expect the error message to refer to "unknown" as the building name.
        assertTrue(error.contains("Storage building 'unknown'"), "The error message should mention 'unknown' as building name.");
    }
}