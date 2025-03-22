package productsimulation.command;

/**
 * CommandRuleChecker is an abstract chain-of-responsibility checker for commands.
 */
public abstract class CommandRuleChecker {
    private final CommandRuleChecker next;

    /**
     * Chains the different command checkers.
     *
     * @param next the next checker in the chain.
     */
    public CommandRuleChecker(CommandRuleChecker next) {
        this.next = next;
    }

    /**
     * Each concrete checker implements its own parsing logic here.
     *
     * @param line the input line (already trimmed).
     * @return a Command if recognized, or null otherwise.
     */
    protected abstract Command checkMyRule(String line);

    /**
     * Checks the input by calling the current rule, then the rest of the chain if not recognized.
     *
     * @param line the input line.
     * @return a Command if recognized, or null if none of the chain recognized it.
     */
    public Command checkInput(String line) {
        Command cmd = checkMyRule(line);
        if (cmd != null) {
            return cmd;
        }
        if (next != null) {
            return next.checkInput(line);
        }
        return null;
    }
}
