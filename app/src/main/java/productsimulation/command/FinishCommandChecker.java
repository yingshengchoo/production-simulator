package productsimulation.command;

public class FinishCommandChecker extends CommandRuleChecker {

    public FinishCommandChecker(CommandRuleChecker next) {
        super(next);
    }

    @Override
    protected Command checkMyRule(String line) {
        if ("finish".equals(line.trim())) {
            return new FinishCommand();
        }
        return null;
    }
}
