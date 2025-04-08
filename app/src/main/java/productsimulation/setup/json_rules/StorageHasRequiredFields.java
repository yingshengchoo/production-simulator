package productsimulation.setup.json_rules;

import com.fasterxml.jackson.databind.JsonNode;

public class StorageHasRequiredFields extends InputRuleChecker {

    public StorageHasRequiredFields(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Check if the "buildings" field exists and is an array.
        if (!root.has("buildings") || !root.get("buildings").isArray()) {
            // Nothing to check if there are no buildings.
            return null;
        }

        // Iterate through each building in the "buildings" array.
        for (JsonNode building : root.get("buildings")) {
            // If the building is defined as a storage building (indicated by "stores" field)
            if (building.has("stores")) {
                String buildingName = building.has("name") ? building.get("name").asText() : "unknown";

                // Check that the "stores" field is present, a non-empty string.
                if (!building.get("stores").isTextual() || building.get("stores").asText().trim().isEmpty()) {
                    return "Storage building '" + buildingName + "' must have a valid 'stores' field.";
                }

                // Check that the "capacity" field exists.
                if (!building.has("capacity")) {
                    return "Storage building '" + buildingName + "' must have a 'capacity' field.";
                }
                // Verify that "capacity" can be converted to an integer and is positive.
                if (!building.get("capacity").canConvertToInt()) {
                    return "Storage building '" + buildingName + "' has an invalid 'capacity' (not an integer).";
                }
                int capacity = building.get("capacity").asInt();
                if (capacity <= 0) {
                    return "Storage building '" + buildingName + "' must have a positive 'capacity'.";
                }

                // Check that the "priority" field exists.
                if (!building.has("priority")) {
                    return "Storage building '" + buildingName + "' must have a 'priority' field.";
                }
                // Verify that "priority" is numeric and greater than zero.
                if (!building.get("priority").isNumber()) {
                    return "Storage building '" + buildingName + "' has an invalid 'priority' (not a number).";
                }
                double priority = building.get("priority").asDouble();
                if (priority <= 0) {
                    return "Storage building '" + buildingName + "' must have a positive 'priority'.";
                }

                // Check that the "sources" field exists and is an array.
                if (!building.has("sources")) {
                    return "Storage building '" + buildingName + "' must have a 'sources' field.";
                }
                if (!building.get("sources").isArray()) {
                    return "Storage building '" + buildingName + "' has a 'sources' field that is not an array.";
                }
            }
        }
        return null;
    }
}