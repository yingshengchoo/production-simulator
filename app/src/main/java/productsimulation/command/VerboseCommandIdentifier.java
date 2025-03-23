package productsimulation.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerboseCommandIdentifier extends CommandIdentifier {
    private static final Pattern PATTERN = Pattern.compile("^verbose\\s+(\\d+)$");

    public VerboseCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    protected Command checkFits(String line) {
        Matcher m = PATTERN.matcher(line);
        if (m.matches()) {
            int level = Integer.parseInt(m.group(1));
            return new VerboseCommand(level);
        }
        return null;
    }
}
