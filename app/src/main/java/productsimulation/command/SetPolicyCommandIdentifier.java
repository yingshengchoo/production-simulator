package productsimulation.command;

import productsimulation.request.Policy;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ReadyPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.servePolicy.SjfPolicy;
import productsimulation.request.sourcePolicy.SoleSourcePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.request.sourcePolicy.SourceSimplelat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetPolicyCommandIdentifier extends CommandIdentifier {
    Set<Policy> policies = new HashSet<>(Arrays.asList(
            new FIFOPolicy(), new ReadyPolicy(), new SjfPolicy(),
            new SoleSourcePolicy(), new SourceQLen(), new SourceSimplelat()
    ));


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
        String policy = typeAndPolicy[1].substring(1, typeAndPolicy[1].length() - 1);  // e.g. "'sjf'", "default".
        for (Policy p : policies) {
            if (p.getName().equals(policy)) {
                return new SetPolicyCommand(type, target, p);
            }
        }
        return null;
    }
}
