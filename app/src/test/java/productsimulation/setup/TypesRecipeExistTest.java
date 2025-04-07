package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import productsimulation.setup.json_rules.TypesRecipeExist;

import static org.junit.jupiter.api.Assertions.*;

// Rule 5
 class TypesRecipeExistTest {

    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * Test that a valid JSON input passes the TypeRecipeChecker.
     * In this test, every recipe referenced in the types is defined in the recipes array.
     */
    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "    {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12},"
                + "    {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "    {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": []"
                + "}";
        JsonNode root = parseJson(json);
        TypesRecipeExist checker = new TypesRecipeExist(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when all type recipe references are valid.");
    }

    /**
     * Test that an invalid JSON input fails the TypeRecipeChecker.
     * In this test, a type references a recipe that is not defined in the recipes array.
     */
    @Test
    public void test_checkMyRule_invalid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "    {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "    {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": []"
                + "}";
        JsonNode root = parseJson(json);
        TypesRecipeExist checker = new TypesRecipeExist(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a type references an unknown recipe.");
        assertEquals("Type 'FactoryType1' references unknown recipe: door", result);
    }
}
