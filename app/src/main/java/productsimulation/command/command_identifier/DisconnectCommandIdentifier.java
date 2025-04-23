package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.DisconnectCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Identifies and creates a DisconnectCommand from input of the form:
 *   disconnect 'Source' to 'Destination'
 */
public class DisconnectCommandIdentifier extends CommandIdentifier {
    private static final Pattern DISCONNECT_PATTERN =
            Pattern.compile("^disconnect\\s+'(.+)'\\s+to\\s+'(.+)'$", Pattern.CASE_INSENSITIVE);

    public DisconnectCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        Matcher m = DISCONNECT_PATTERN.matcher(line.trim());
        if (!m.matches()) {
            return null;
        }
        String src = m.group(1);
        String dst = m.group(2);
        return new DisconnectCommand(src, dst);
    }
}