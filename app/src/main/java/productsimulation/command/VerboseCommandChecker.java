package productsimulation.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerboseCommandChecker extends CommandRuleChecker {
    private static final Pattern PATTERN = Pattern.compile("^verbose\\s+(\\d+)$");

    public VerboseCommandChecker(CommandRuleChecker next) {
        super(next);
    }

    @Override
    protected Command checkMyRule(String line) {
        Matcher m = PATTERN.matcher(line);
        if (m.matches()) {
            int level = Integer.parseInt(m.group(1));
            // Optionally restrict to 0..2
            return new VerboseCommand(level);
        }
        return null;
    }
}
