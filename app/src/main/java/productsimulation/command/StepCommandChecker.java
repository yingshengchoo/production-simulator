package productsimulation.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepCommandChecker extends CommandRuleChecker {
    private static final Pattern PATTERN = Pattern.compile("^step\\s+(\\d+)$");

    public StepCommandChecker(CommandRuleChecker next) {
        super(next);
    }

    @Override
    protected Command checkMyRule(String line) {
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
