package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class AllRequiredFieldsArePresentAndLegalTest {
    private JsonNode parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }
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
        AllRequiredFieldsArePresentAndLegal checker = new AllRequiredFieldsArePresentAndLegal(null);
        String result = checker.checkInput(root);
        assertNull(result);
    }

    private void checkMyRuleHelper_invalid_cases(List<String> jsons, List<String> expectedResult) throws Exception {
        for (int i = 0; i < jsons.size(); i++) {
            JsonNode root = parseJson(jsons.get(i));
            AllRequiredFieldsArePresentAndLegal checker = new AllRequiredFieldsArePresentAndLegal(null);
            String result = checker.checkInput(root);
            assertEquals(expectedResult.get(i), result);
        }
    }

    @Test
    public void test_checkMyRule_building_error() throws Exception {
        List<String> jsons = new ArrayList<>();
        List<String> expectedResult = new ArrayList<>();
        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "]"
                + "}";
        String expectedRes = "Buildings should be present";
        jsons.add(json);
        expectedResult.add(expectedRes);

        String json2 = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": "
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + ""
                + "}";
        String expectedRes2 = "Buildings should be an array";
        jsons.add(json2);
        expectedResult.add(expectedRes2);

        String json3 = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  { \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String expectedRes3 = "A building should have a name";
        jsons.add(json3);
        expectedResult.add(expectedRes3);

        String json4 = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\",  \"sources\": []}"
                + "]"
                + "}";
        String expectedRes4 = "A building should have a type or a mine";
        jsons.add(json4);
        expectedResult.add(expectedRes4);

        String json5 = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\"}"
                + "]"
                + "}";
        String expectedRes5 = "A building should have a source";
        jsons.add(json5);
        expectedResult.add(expectedRes5);

        String json6 = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": \"fddf\"}"
                + "]"
                + "}";
        String expectedRes6 = "A building should have a list of source";
        jsons.add(json6);
        expectedResult.add(expectedRes6);

        checkMyRuleHelper_invalid_cases(jsons, expectedResult);


    }

    @Test
    public void test_checkMyRule_type_error() throws Exception {
        List<String> jsons = new ArrayList<>();
        List<String> expectedResults = new ArrayList<>();

        String json = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp = "Types should be present";
        jsons.add(json);
        expectedResults.add(exp);

        String json2 = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\":  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]},"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp2 = "Types should be an array";
        jsons.add(json2);
        expectedResults.add(exp2);

        String json3 = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\":  [{ \"recipes\": [\"door\"]}],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp3 = "A type should have a name";
        jsons.add(json3);
        expectedResults.add(exp3);

        String json4 =  "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": \"door\"}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp4 = "A type should have a list of recipes";
        jsons.add(json4);
        expectedResults.add(exp4);
        checkMyRuleHelper_invalid_cases(jsons, expectedResults);


    }

    @Test
    public void test_checkMyRule_recipe_error() throws Exception {
        List<String> jsons = new ArrayList<>();
        List<String> expectedResults = new ArrayList<>();

        String json = "{"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp1 = "Recipes should be present";
        jsons.add(json);
        expectedResults.add(exp1);


        String json2 = "{"
                + "\"recipes\": "
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp2 = "Recipes should be an array";
        jsons.add(json2);
        expectedResults.add(exp2);

        String json3 = "{"
                + "\"recipes\": ["
                + "  { \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp3 = "A recipe should have an output";
        jsons.add(json3);
        expectedResults.add(exp3);

        String json4 =  "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"ingredients\": {\"wood\":1, \"handle\":1}, \"latency\":\"1\"},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp4 =  "A recipe should have a latency as an int";
        jsons.add(json4);
        expectedResults.add(exp4);

        String json5 = "{"
                + "\"recipes\": ["
                + "  {\"output\": \"door\", \"latency\":12},"
                + "  {\"output\": \"wood\", \"ingredients\": {}, \"latency\":1}"
                + "],"
                + "\"types\": ["
                + "  {\"name\": \"FactoryType1\", \"recipes\": [\"door\"]}"
                + "],"
                + "\"buildings\": ["
                + "  {\"name\": \"Factory1\", \"type\": \"FactoryType1\", \"sources\": []}"
                + "]"
                + "}";
        String exp5 = "A recipe should have ingredients";
        jsons.add(json5);
        expectedResults.add(exp5);
        checkMyRuleHelper_invalid_cases(jsons, expectedResults);
    }
}