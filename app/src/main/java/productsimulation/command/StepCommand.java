package productsimulation.command;

import productsimulation.LogicTime;

public class StepCommand extends Command {
    private int step;
    public StepCommand(int step) {
        this.step = step;
    }

    @Override
    public void execute() {
        LogicTime.getInstance().stepNHandler(step);
    }
}
