package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.RemoveBuildingCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Identifies and creates a RemoveBuildingCommand from input of the form:
 *   remove 'BuildingName'
 */
public class RemoveBuildingCommandIdentifier extends CommandIdentifier {
    private static final Pattern REMOVE_PATTERN =
            Pattern.compile("^remove\\s+'(.+)'$", Pattern.CASE_INSENSITIVE);

    public RemoveBuildingCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        Matcher m = REMOVE_PATTERN.matcher(line.trim());
        if (!m.matches()) {
            return null;
        }
        String name = m.group(1);
        return new RemoveBuildingCommand(name);
    }
}
