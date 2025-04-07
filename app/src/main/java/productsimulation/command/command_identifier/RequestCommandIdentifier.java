package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.RequestCommand;

public class RequestCommandIdentifier extends CommandIdentifier {
    public RequestCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        String trimmedLine = line.trim();
        // Verify the command starts with "request "
        if (!trimmedLine.toLowerCase().startsWith("request ")) {
            return null;
        }
        // Remove the "request" keyword
        String remainder = trimmedLine.substring("request".length()).trim();

        // Expecting the item to be enclosed in single quotes
        if (!remainder.startsWith("'")) {
            return null;
        }
        int closingQuoteIndex = remainder.indexOf("'", 1);
        if (closingQuoteIndex == -1) {
            return null;
        }
        String item = remainder.substring(1, closingQuoteIndex);

        // Get the rest of the string after the item
        String afterItem = remainder.substring(closingQuoteIndex + 1).trim();
        // Check that it starts with "from "
        if (!afterItem.toLowerCase().startsWith("from ")) {
            return null;
        }
        // Remove the "from" keyword
        String buildingPart = afterItem.substring("from".length()).trim();
        // The building must be enclosed in single quotes
        if (!(buildingPart.startsWith("'") && buildingPart.endsWith("'"))) {
            return null;
        }
        // Extract the building by removing the surrounding quotes
        String building = buildingPart.substring(1, buildingPart.length() - 1);

        return new RequestCommand(item, building);
    }
}