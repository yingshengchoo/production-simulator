package productsimulation.command;

public class LoadCommandIdentifier extends CommandIdentifier {

    public LoadCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    protected Command checkFits(String line) {
        if (!line.startsWith("load ")) {
            return null;
        }
        String filename = line.substring(5).trim();
        if (!filename.isEmpty()) {
            return new LoadCommand(filename);
        }
        return null;
    }
}
