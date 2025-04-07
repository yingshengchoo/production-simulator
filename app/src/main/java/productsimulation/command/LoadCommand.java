package productsimulation.command;

import productsimulation.State;

import java.io.IOException;

public class LoadCommand extends Command {
    private final String filename;

    public LoadCommand(String filename) {
        this.filename = filename;
    }

    @Override
    public String execute() throws IOException, ClassNotFoundException {
        State.getInstance().load(filename);
        return null;
    }

    public String getFilename() {
        return filename;
    }
}
