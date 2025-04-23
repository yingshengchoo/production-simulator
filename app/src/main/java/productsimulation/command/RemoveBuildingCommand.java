package productsimulation.command;

import productsimulation.State;

/**
 * Command to remove a building from the simulation, cleaning up any
 * pending road connections and then deleting the building itself.
 */
public class RemoveBuildingCommand extends Command {
    private final String name;

    public RemoveBuildingCommand(String name) {
        this.name = name;
    }

    @Override
    public String execute() {
        return State.removeBuildingHandler(name);
    }
}
