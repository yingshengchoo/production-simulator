package productsimulation.command;

import productsimulation.State;

import java.io.IOException;

public class SaveCommand extends Command {
    String filename;

    public SaveCommand(String fileName) {
        this.filename = fileName;
    }

    @Override
    public String execute() throws IOException {
        State.getInstance().save(filename);
        return null;
    }
}
