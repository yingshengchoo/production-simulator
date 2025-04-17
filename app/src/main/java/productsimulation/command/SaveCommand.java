package productsimulation.command;

import productsimulation.State;

import java.io.IOException;

public class SaveCommand extends Command {
    String filename;

    public SaveCommand(String fileName) {
        this.filename = fileName;
    }

    @Override
    public String execute() {
        try {
            State.getInstance().save(filename);
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        return null;
    }
}
