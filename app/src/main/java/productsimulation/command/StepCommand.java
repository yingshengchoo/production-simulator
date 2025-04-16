package productsimulation.command;

import productsimulation.LogicTime;

public class StepCommand extends Command {
    private int step;
    public StepCommand(int step) {
        this.step = step;
    }

    @Override
    public String execute() {
        return LogicTime.getInstance().stepNHandler(step);
    }

    public int getSteps() {
        return step;
    }
}
