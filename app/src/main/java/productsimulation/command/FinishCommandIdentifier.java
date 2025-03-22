package productsimulation.command;

public class FinishCommandIdentifier extends CommandIdentifier {

    public FinishCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    protected Command checkFits(String line) {
        if ("finish".equals(line.trim())) {
            return new FinishCommand();
        }
        return null;
    }
}
