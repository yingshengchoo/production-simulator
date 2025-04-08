package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import productsimulation.setup.json_rules.FactoryCanGetAllIngredientsItNeeds;

import static org.junit.jupiter.api.Assertions.*;

 class FactoryCanGetAllIngredientsItNeedsTest {

    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    /**
     * Test a valid configuration where a factory has multiple sources.
     * In this updated test:
     * - "door" requires "handle" and "frame".
     * - Type T1 produces "door", "handle", and "frame".
     * - Factory1 now lists itself, Factory2, Mine1, and Mine2 as sources.
     *   Factory1 (by self-reference) can produce "frame" and "handle" if needed,
     *   Mine1 produces "wood", and Mine2 produces "metal".
     * Thus, Factory1 can source all required ingredients.
     */
    @Test
    public void test_valid_multipleSources() throws Exception {
        String json = "{" +
                "\"recipes\": [" +
                "  {\"output\": \"door\", \"ingredients\": {\"handle\":1, \"frame\":1}, \"latency\":12}," +
                "  {\"output\": \"handle\", \"ingredients\": {\"metal\":1}, \"latency\":5}," +
                "  {\"output\": \"frame\", \"ingredients\": {\"wood\":1}, \"latency\":8}," +
                "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}," +
                "  {\"output\": \"metal\", \"ingredients\": {}, \"latency\":1}" +
                "]," +
                "\"types\": [" +
                "  {\"name\": \"T1\", \"recipes\": [\"door\", \"handle\", \"frame\"]}," +
                "  {\"name\": \"T2\", \"recipes\": [\"handle\"]}" +
                "]," +
                "\"buildings\": [" +
                "  {\"name\": \"Factory1\", \"type\": \"T1\", \"sources\": [\"Factory1\", \"Factory2\", \"Mine1\", \"Mine2\"]}," +
                "  {\"name\": \"Factory2\", \"type\": \"T2\", \"sources\": [\"Mine2\"]}," +
                "  {\"name\": \"Mine1\", \"mine\": \"wood\"}," +
                "  {\"name\": \"Mine2\", \"mine\": \"metal\"}" +
                "]" +
                "}";
        JsonNode root = parseJson(json);
        FactoryCanGetAllIngredientsItNeeds checker = new FactoryCanGetAllIngredientsItNeeds(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when required ingredients can be sourced.");
    }

    /**
     * Test an invalid configuration where a factory's only source cannot produce one required ingredient.
     * In this example, Factory1 (type T1) requires "handle" (from "door") but only lists Mine1 (which produces wood).
     */
    @Test
    public void test_invalid_missingIngredient() throws Exception {
        String json = "{" +
                "\"recipes\": [" +
                "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12}," +
                "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}," +
                "  {\"output\": \"handle\", \"ingredients\": {\"wood\":1}, \"latency\":5}" +
                "]," +
                "\"types\": [" +
                "  {\"name\": \"T1\", \"recipes\": [\"door\", \"handle\"]}" +
                "]," +
                "\"buildings\": [" +
                "  {\"name\": \"Factory1\", \"type\": \"T1\", \"sources\": [\"Mine1\"]}," +
                "  {\"name\": \"Mine1\", \"mine\": \"wood\"}" +
                "]" +
                "}";
        JsonNode root = parseJson(json);
        FactoryCanGetAllIngredientsItNeeds checker = new FactoryCanGetAllIngredientsItNeeds(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a required ingredient cannot be sourced.");
        assertEquals("Factory building 'Factory1' cannot source ingredient 'handle' required by recipe 'door'.", result);
    }

    /**
     * Test an invalid configuration where a factory has no "sources" field.
     * This should trigger an error indicating that the factory does not have sources.
     */
    @Test
    public void test_invalid_noSourcesField() throws Exception {
        String json = "{" +
                "\"recipes\": [" +
                "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12}" +
                "]," +
                "\"types\": [" +
                "  {\"name\": \"T1\", \"recipes\": [\"door\"]}" +
                "]," +
                "\"buildings\": [" +
                "  {\"name\": \"Factory1\", \"type\": \"T1\"}" +
                "]" +
                "}";
        JsonNode root = parseJson(json);
        FactoryCanGetAllIngredientsItNeeds checker = new FactoryCanGetAllIngredientsItNeeds(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a factory has no sources field.");
        assertEquals("Factory building 'Factory1' does not have sources but requires ingredient: wood", result);
    }

    /**
     * Test that non-factory buildings (e.g., mines) are ignored by this checker.
     * Even if a mine has non-empty "sources", this rule should not apply.
     */
    @Test
    public void test_valid_nonFactoryIgnored() throws Exception {
        String json = "{" +
                "\"recipes\": [" +
                "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}" +
                "]," +
                "\"types\": []," +
                "\"buildings\": [" +
                "  {\"name\": \"Mine1\", \"mine\": \"wood\", \"sources\": [\"SomeSource\"]}" +
                "]" +
                "}";
        JsonNode root = parseJson(json);
        FactoryCanGetAllIngredientsItNeeds checker = new FactoryCanGetAllIngredientsItNeeds(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error for non-factory building even if sources is non-empty.");
    }


    /**
     * Test when typeNode is null.
     * A factory building references a type that is not defined.
     * Our implementation skips checking such a building, so no error is returned.
     */
    @Test
    public void test_typeNodeNull() throws Exception {
        // No types provided, but Factory1 claims type "T1"
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12}"
                + "],"
                + "\"types\": [],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"T1\", \"sources\": [\"Mine1\"]},"
                + "  {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        FactoryCanGetAllIngredientsItNeeds checker = new FactoryCanGetAllIngredientsItNeeds(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when building's type is undefined (typeNode==null).");
    }

    /**
     * Test when recipeNode is null.
     * A type references a recipe that is not defined in the recipes array.
     * Our implementation skips such recipes, so no error is returned.
     */
    @Test
    public void test_recipeNodeNull() throws Exception {
        // Type T1 references a recipe "nonexistent" that isn't in the recipes array.
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"T1\", \"recipes\": [\"nonexistent\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"T1\", \"sources\": [\"Mine1\"]},"
                + "  {\"name\": \"Mine1\", \"mine\": \"wood\"}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        FactoryCanGetAllIngredientsItNeeds checker = new FactoryCanGetAllIngredientsItNeeds(null);
        String result = checker.checkInput(root);
        assertNull(result, "Expected no error when a type references a recipe that is undefined (recipeNode==null).");
    }

    /**
     * Test when srcBuilding is null.
     * A factory's "sources" array references a building that is not defined.
     * This should cause an error because no valid source can produce the required ingredient.
     */
    @Test
    public void test_srcBuildingNull() throws Exception {
        // Factory1 references type T1, and T1's "door" recipe requires "wood".
        // The only source is "Nonexistent", which is not defined.
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"T1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"T1\", \"sources\": [\"Nonexistent\"]}"
                + "]"
                + "}";
        JsonNode root = parseJson(json);
        FactoryCanGetAllIngredientsItNeeds checker = new FactoryCanGetAllIngredientsItNeeds(null);
        String result = checker.checkInput(root);
        assertNotNull(result, "Expected an error when a factory's source is undefined (srcBuilding==null).");
        assertEquals("Factory building 'Factory1' cannot source ingredient 'wood' required by recipe 'door'.", result);
    }
}
