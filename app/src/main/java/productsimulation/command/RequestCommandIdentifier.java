package productsimulation.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestCommandIdentifier extends CommandIdentifier {
    // Regex for: request 'SOMETHING' from 'SOMETHING'
    private static final Pattern PATTERN =
            Pattern.compile("^request\\s+'([^']+)'\\s+from\\s+'([^']+)'\\s*$");

    public RequestCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    protected Command checkFits(String line) {
        Matcher m = PATTERN.matcher(line);
        if (m.matches()) {
            String item = m.group(1);
            String building = m.group(2);
            return new RequestCommand(item, building);
        }
        return null;
    }
}
