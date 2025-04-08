package productsimulation.command;

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
        return null;
    }
}