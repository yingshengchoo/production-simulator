package productsimulation.command;

import productsimulation.LogicTime;

public class FinishCommand extends Command {

    public FinishCommand() {
    }

    @Override
    public String execute() {
        LogicTime.getInstance().finishHandler();
        return null;
    }
}
