package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.VerboseCommand;

public class VerboseCommandIdentifier extends CommandIdentifier {

    public VerboseCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        // Trim the input to remove extra spaces.
        String trimmedLine = line.trim();

        // Check if the command starts with "verbose " (case-insensitive).
        if (!trimmedLine.toLowerCase().startsWith("verbose ")) {
            return null;
        }

        // Remove the "verbose" keyword and trim the remainder.
        String numberPart = trimmedLine.substring("verbose".length()).trim();
        if (numberPart.isEmpty()) {
            return null;
        }

        // Ensure that the numberPart contains only digits.
        for (int i = 0; i < numberPart.length(); i++) {
            if (!Character.isDigit(numberPart.charAt(i))) {
                return null;
            }
        }

        // Attempt to parse the integer value.
        try {
            int level = Integer.parseInt(numberPart);
            return new VerboseCommand(level);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}