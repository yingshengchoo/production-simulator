package productsimulation.setup.json_rules;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashSet;
import java.util.Set;
import productsimulation.Coordinate;

public class NoRepetitiveCoordinate extends InputRuleChecker {

    public NoRepetitiveCoordinate(InputRuleChecker next) {
        super(next);
    }

    @Override
    protected String checkMyRule(JsonNode root) {
        // Check that the "buildings" field exists and is an array.
        if (!root.has("buildings") || !root.get("buildings").isArray()) {
            return null; // Other rules can handle missing buildings.
        }

        Set<Coordinate> usedCoordinates = new HashSet<>();

        for (JsonNode building : root.get("buildings")) {
            // Only consider buildings that have both "x" and "y" defined.
            if (building.has("x") && building.has("y")) {
                int x = building.get("x").asInt();
                int y = building.get("y").asInt();
                Coordinate coord = new Coordinate(x, y);

                // If this coordinate has been used already, return an error message.
                if (usedCoordinates.contains(coord)) {
                    String buildingName = building.has("name") ? building.get("name").asText() : "unknown";
                    return "Duplicate coordinate found: " + coord + " in building '" + buildingName + "'.";
                }

                // Otherwise, record the coordinate.
                usedCoordinates.add(coord);
            }
        }

        // No duplicates were found.
        return null;
    }
}