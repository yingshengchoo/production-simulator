package productsimulation.command;

public class LoadCommand extends Command {
    private final String filename;

    public LoadCommand(String filename) {
        this.filename = filename;
    }

    @Override
    public void execute() {
        // Implementation: ask the simulation to restore from 'filename'
        System.out.println("Executing LoadCommand, loading from: " + filename);
    }

    public String getFilename() {
        return filename;
    }
}
