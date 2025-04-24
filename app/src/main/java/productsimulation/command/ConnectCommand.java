package productsimulation.command;

import productsimulation.model.road.Road;
import productsimulation.model.road.RoadHandler;

public class ConnectCommand extends Command {
    private final String source;
    private final String destination;

    public ConnectCommand(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String execute() {
         return RoadHandler.connectHandler(source, destination);
    }
}