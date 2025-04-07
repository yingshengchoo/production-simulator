package productsimulation.command;

import productsimulation.Log;

public class VerboseCommand extends Command {
    private final int verboseLevel;

    public VerboseCommand(int verboseLevel) {
        this.verboseLevel = verboseLevel;
    }

    @Override
    public String execute() {
        Log.setLogLevel(verboseLevel);
        return null;
    }

    public int getLevel() {
        return verboseLevel;
    }
}
