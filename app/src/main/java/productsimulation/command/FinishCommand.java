package productsimulation.command;

import productsimulation.Log;
import productsimulation.LogicTime;

public class FinishCommand extends Command {

    public FinishCommand() {
    }

    @Override
    public void execute() {
        LogicTime.getInstance().finishHandler();
    }
}
