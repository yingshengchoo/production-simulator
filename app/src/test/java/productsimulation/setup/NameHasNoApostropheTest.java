package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import productsimulation.setup.json_rules.NameHasNoApostrophe;

import static org.junit.jupiter.api.Assertions.*;

 class NameHasNoApostropheTest {

    private JsonNode getJsonNode(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * Test that valid JSON input (with no names containing an apostrophe) passes the IllegalCharacterChecker.
     */
    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{"
                + "\"recipes\": [ { \"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12 } ],"
                + "\"types\": [ { \"name\": \"FactoryType1\" } ],"
                + "\"buildings\": [ { \"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": [] } ]"
                + "}";
        JsonNode root = getJsonNode(json);
        NameHasNoApostrophe checker = new NameHasNoApostrophe(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when no names contain apostrophes.");
    }

    /**
     * Test that a recipe output containing an apostrophe causes an error.
     */
    @Test
    public void test_checkMyRule_invalid_recipe() throws Exception {
        String json = "{"
                + "\"recipes\": [ { \"output\": \"do'or\", \"ingredients\": {\"wood\":1}, \"latency\":12 } ],"
                + "\"types\": [ { \"name\": \"FactoryType1\" } ],"
                + "\"buildings\": [ { \"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": [] } ]"
                + "}";
        JsonNode root = getJsonNode(json);
        NameHasNoApostrophe checker = new NameHasNoApostrophe(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a recipe output contains an apostrophe.");
        assertEquals("Recipe output name contains illegal character: do'or", result);
    }

    /**
     * Test that a type name containing an apostrophe causes an error.
     */
    @Test
    public void test_checkMyRule_invalid_type() throws Exception {
        String json = "{"
                + "\"recipes\": [ { \"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12 } ],"
                + "\"types\": [ { \"name\": \"Fac'toryType1\" } ],"
                + "\"buildings\": [ { \"name\": \"Factory1\", \"type\": \"Fac'toryType1\", \"sources\": [] } ]"
                + "}";
        JsonNode root = getJsonNode(json);
        NameHasNoApostrophe checker = new NameHasNoApostrophe(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a type name contains an apostrophe.");
        assertEquals("Type name contains illegal character: Fac'toryType1", result);
    }

    /**
     * Test that a building name containing an apostrophe causes an error.
     */
    @Test
    public void test_checkMyRule_invalid_building() throws Exception {
        String json = "{"
                + "\"recipes\": [ { \"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12 } ],"
                + "\"types\": [ { \"name\": \"FactoryType1\" } ],"
                + "\"buildings\": [ { \"name\": \"Fac'tory1\", \"type\": \"FactoryType1\", \"sources\": [] } ]"
                + "}";
        JsonNode root = getJsonNode(json);
        NameHasNoApostrophe checker = new NameHasNoApostrophe(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a building name contains an apostrophe.");
        assertEquals("Building name contains illegal character: Fac'tory1", result);
    }
}
