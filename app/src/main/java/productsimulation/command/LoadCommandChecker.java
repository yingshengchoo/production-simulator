package productsimulation.command;

public class LoadCommandChecker extends CommandRuleChecker {

    public LoadCommandChecker(CommandRuleChecker next) {
        super(next);
    }

    @Override
    protected Command checkMyRule(String line) {
        if (!line.startsWith("load ")) {
            return null;
        }
        String filename = line.substring(5).trim();
        if (!filename.isEmpty()) {
            return new LoadCommand(filename);
        }
        return null;
    }
}
