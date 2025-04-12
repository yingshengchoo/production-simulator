package productsimulation.command;

import productsimulation.State;

import java.io.IOException;

public class LoadCommand extends Command {
    private final String filename;

    public LoadCommand(String filename) {
        this.filename = filename;
    }

    @Override
    public String execute() {
        try {
            State.getInstance().load(filename);
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    public String getFilename() {
        return filename;
    }
}
