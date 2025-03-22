package productsimulation.command;

public class SetPolicyCommandIdentifier extends CommandIdentifier {

    public SetPolicyCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    protected Command checkFits(String line) {
        // Must begin with "set policy "
        if (!line.startsWith("set policy ")) {
            return null;
        }
        // remove prefix
        String rest = line.substring(10).trim(); // everything after "set policy"

        // We expect something like: "request 'sjf' on default" or "source default on *" etc.
        // Minimal check for " on ":
        if (!rest.contains(" on ")) {
            return null;
        }
        String[] parts = rest.split("\\s+on\\s+", 2);
        if (parts.length != 2) {
            return null;
        }
        // left is e.g. "request 'sjf'"
        // right is e.g. "default"
        String left = parts[0].trim();
        String right = parts[1].trim();

        // The left part should have a TYPE and a POLICY
        // e.g. "request 'sjf'"
        String[] leftParts = left.split("\\s+", 2);
        if (leftParts.length < 2) {
            return null;
        }
        String type = leftParts[0];    // "request" or "source"
        String policy = leftParts[1];  // e.g. "'sjf'", "default", etc.

        // The target is right
        String target = right; // e.g. "'door factory'", "*", "default"

        return new SetPolicyCommand(type, policy, target);
    }
}
