package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.SaveCommand;

public class SaveCommandIdentifier extends CommandIdentifier {

    public SaveCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        if (!line.startsWith("save ")) {
            return null;
        }
        String filename = line.substring(5).trim();
        if (!filename.isEmpty()) {
            return new SaveCommand(filename);
        }
        return null;
    }
}
