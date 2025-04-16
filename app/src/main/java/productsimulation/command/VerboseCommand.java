package productsimulation.command;

import productsimulation.Log;
import productsimulation.State;

public class VerboseCommand extends Command {
    private final int verboseLevel;

    public VerboseCommand(int verboseLevel) {
        this.verboseLevel = verboseLevel;
    }

    @Override
    public String execute() {
        try {
            Log.setLogLevel(verboseLevel);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    public int getLevel() {
        return verboseLevel;
    }
}
