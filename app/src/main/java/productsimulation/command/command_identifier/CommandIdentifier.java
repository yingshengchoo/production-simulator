package productsimulation.command.command_identifier;

import productsimulation.command.Command;

public abstract class CommandIdentifier {
    private final CommandIdentifier next;

    public CommandIdentifier(CommandIdentifier next) {
        this.next = next;
    }

    public abstract Command checkFits(String line);

    public Command checkInput(String line) {

        Command cmd = checkFits(line);
        if (cmd != null) {
            return cmd;
        }
        if (next != null) {
            return next.checkInput(line);
        }
        return null;
    }
}
