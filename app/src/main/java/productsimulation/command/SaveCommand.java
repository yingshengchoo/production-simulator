package productsimulation.command;

public class SaveCommand extends Command {
    String filename;

    public SaveCommand(String fileName) {
        this.filename = fileName;
    }

    @Override
    public void execute() {}
}
