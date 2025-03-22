package productsimulation.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepCommandIdentifier extends CommandIdentifier {
    private static final Pattern PATTERN = Pattern.compile("^step\\s+(\\d+)$");

    public StepCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    protected Command checkFits(String line) {
        Matcher m = PATTERN.matcher(line);
        if (m.matches()) {
            long num = Long.parseLong(m.group(1));
            if (num < 1 || num >= Integer.MAX_VALUE) {
                return null;
            }
            return new StepCommand((int) num);
        }
        return null;
    }
}
