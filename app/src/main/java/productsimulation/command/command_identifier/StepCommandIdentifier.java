package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.StepCommand;

public class StepCommandIdentifier extends CommandIdentifier {

    public StepCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        // Trim the line to remove extra spaces
        String trimmedLine = line.trim();

        // Check if the command starts with "step" (case-insensitive)
        if (!trimmedLine.toLowerCase().startsWith("step ")) {
            return null;
        }

        // Remove the "step" keyword and trim the rest to isolate the number part
        String numberPart = trimmedLine.substring("step".length()).trim();
        if (numberPart.isEmpty()) {
            return null;
        }

        // Validate that every character in numberPart is a digit
        for (int i = 0; i < numberPart.length(); i++) {
            if (!Character.isDigit(numberPart.charAt(i))) {
                return null;
            }
        }

        // Attempt to parse the number
        try {
            long num = Long.parseLong(numberPart);
            if (num < 1 || num >= Integer.MAX_VALUE) {
                return null;
            }
            return new StepCommand((int) num);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}