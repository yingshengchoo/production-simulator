package productsimulation.command;

import productsimulation.Log;

public class VerboseCommand extends Command {
    private final int verboseLevel;

    public VerboseCommand(int verboseLevel) {
        this.verboseLevel = verboseLevel;
    }

    @Override
    public void execute() {
        Log.setLogLevel(verboseLevel);
    }

    public int getLevel() {
        return verboseLevel;
    }
}
