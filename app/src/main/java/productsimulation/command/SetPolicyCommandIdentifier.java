package productsimulation.command;

public class SetPolicyCommandIdentifier extends CommandIdentifier {

    public SetPolicyCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    protected Command checkFits(String line) {
        if (!line.startsWith("set policy ")) {
            return null;
        }
        // remove prefix
        String rest = line.substring(10).trim();

        if (!rest.contains(" on ")) {
            return null;
        }
        String[] parts = rest.split("\\s+on\\s+", 2);
        if (parts.length != 2) {
            return null;
        }

        String target = parts[1].trim();

        //  TYPE and a POLICY
        // e.g. "request 'sjf'"
        String[] typeAndPolicy = parts[0].trim().split("\\s+", 2);
        if (typeAndPolicy.length < 2) {
            return null;
        }
        String type = typeAndPolicy[0];    // "request" or "source"
        String policy = typeAndPolicy[1];  // e.g. "'sjf'", "default".

        return new SetPolicyCommand(type, policy, target);
    }
}
