package productsimulation.command;

public class SaveCommandChecker extends CommandRuleChecker {

    public SaveCommandChecker(CommandRuleChecker next) {
        super(next);
    }

    @Override
    protected Command checkMyRule(String line) {
//        if (!line.startsWith("save ")) {
//            return null;
//        }
//        String filename = line.substring(5).trim();
//        if (!filename.isEmpty()) {
//            return new SaveCommand(filename);
//        }
        return null;
    }
}
