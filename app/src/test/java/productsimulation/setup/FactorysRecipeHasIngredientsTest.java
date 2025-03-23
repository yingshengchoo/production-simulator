package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Input Validation Rule 7
 */
 class FactorysRecipeHasIngredientsTest {

    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * A valid input, where a factory type references a recipe with ingredients
     */
    @Test
    public void test_checkMyRule_valid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        FactorysRecipeHasIngredients checker = new FactorysRecipeHasIngredients(null);
        String result = checker.checkInput(root);
        // Expect no error message.
        assertNull(result, "Expected no error for valid factory recipe references.");
    }

    /**
     * An invalid input, where a factory type references a recipe with no ingredients
     */
    @Test
    public void test_checkMyRule_invalid() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {}, \"latency\":12}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        FactorysRecipeHasIngredients checker = new FactorysRecipeHasIngredients(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error for factory referencing a recipe with no ingredients.");
        assertEquals("Factory type 'FactoryType1' references recipe 'door' which has no ingredients.", result);
    }

    @Test
    public void test_checkMyRule_hasNoRecipesNode() throws Exception {
        String json = "{"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        FactorysRecipeHasIngredients checker = new FactorysRecipeHasIngredients(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error for factory referencing a recipe with no ingredients.");
        assertEquals("Recipes field is missing or invalid.", result);
    }

    @Test
    public void test_checkMyRule_hasNoTypesNode() throws Exception {
        String json = "{"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        FactorysRecipeHasIngredients checker = new FactorysRecipeHasIngredients(null);
        String result = checker.checkInput(root);
        assertNull(result);
    }

    @Test
    public void test_checkMyRule_cantFindRecipeByOutput() throws Exception {
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door1\", \"ingredients\": {}, \"latency\":12}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        FactorysRecipeHasIngredients checker = new FactorysRecipeHasIngredients(null);
        String result = checker.checkInput(root);
        assertEquals("Factory type '" + "FactoryType1" + "' references undefined recipe '" + "door" + "'.", result);    }
 }
