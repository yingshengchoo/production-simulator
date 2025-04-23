package productsimulation.command;
import productsimulation.model.road.Road;

/**
 * Command to remove an existing connection between two buildings.
 */
public class DisconnectCommand extends Command {
    private final String source;
    private final String destination;

    public DisconnectCommand(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    public String getSource() { return source; }
    public String getDestination() { return destination; }

    @Override
    public String execute() {
        return Road.disconnectHandler(source, destination);
    }
}
