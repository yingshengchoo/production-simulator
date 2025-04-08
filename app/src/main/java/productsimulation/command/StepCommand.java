package productsimulation.command;

import productsimulation.LogicTime;

public class StepCommand extends Command {
    private int step;
    public StepCommand(int step) {
        this.step = step;
    }

    @Override
    public String execute() {
        LogicTime.getInstance().stepNHandler(step);
        return null;
    }

    public int getSteps() {
        return step;
    }
}
