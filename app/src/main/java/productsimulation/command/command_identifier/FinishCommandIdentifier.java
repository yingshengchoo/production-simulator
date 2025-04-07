package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.FinishCommand;

public class FinishCommandIdentifier extends CommandIdentifier {

    public FinishCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        if ("finish".equals(line.trim())) {
            return new FinishCommand();
        }
        return null;
    }
}
