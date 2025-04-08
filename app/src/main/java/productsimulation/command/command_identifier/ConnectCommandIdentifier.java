package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.ConnectCommand;

public class ConnectCommandIdentifier extends CommandIdentifier {

    public ConnectCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        String trimmedLine = line.trim();
        // Check if the command starts with "connect "
        if (!trimmedLine.toLowerCase().startsWith("connect ")) {
            return null;
        }

        // Remove the "connect" keyword
        String remainder = trimmedLine.substring("connect".length()).trim();
        // The remainder should start with a single quote for the source building name.
        if (!remainder.startsWith("'")) {
            return null;
        }

        // Find the closing quote for the source name.
        int closingQuoteIndex = remainder.indexOf("'", 1);
        if (closingQuoteIndex < 0) {
            return null;
        }

        String sourceName = remainder.substring(1, closingQuoteIndex);
        // Remove the parsed source, then trim.
        String afterSource = remainder.substring(closingQuoteIndex + 1).trim();

        // Check that the next token is "to" (case-insensitive) followed by at least one whitespace.
        if (!afterSource.toLowerCase().startsWith("to ")) {
            return null;
        }

        // Remove "to" and trim to get the destination part.
        String destPart = afterSource.substring(2).trim();
        // The destination must be enclosed in single quotes.
        if (!destPart.startsWith("'")) {
            return null;
        }

        int destClosingQuoteIndex = destPart.indexOf("'", 1);
        if (destClosingQuoteIndex < 0) {
            return null;
        }

        String destName = destPart.substring(1, destClosingQuoteIndex);

        // At this point, we have extracted source and destination names.
        return new ConnectCommand(sourceName, destName);
    }
}