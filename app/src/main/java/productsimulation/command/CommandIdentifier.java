package productsimulation.command;

public abstract class CommandIdentifier {
    private final CommandIdentifier next;

    /**
     * Chains the different command checkers.
     *
     * @param next the next checker in the chain.
     */
    public CommandIdentifier(CommandIdentifier next) {
        this.next = next;
    }

    /**
     * Each concrete checker implements its own parsing logic here.
     *
     * @param line the input line (already trimmed).
     * @return a Command if recognized, or null otherwise.
     */
    protected abstract Command checkFits(String line);

    /**
     * Checks the input by calling the current rule, then the rest of the chain if not recognized.
     *
     * @param line the input line.
     * @return a Command if recognized, or null if none of the chain recognized it.
     */
    public Command checkInput(String line) {

        Command cmd = checkFits(line);
        if (cmd != null) {
            return cmd;
        }

        if (next != null) {
            return next.checkInput(line);
        }


        return null;
    }
}
