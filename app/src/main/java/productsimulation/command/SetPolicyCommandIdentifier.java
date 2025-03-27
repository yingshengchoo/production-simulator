package productsimulation.command;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import productsimulation.request.Policy;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ReadyPolicy;
import productsimulation.request.servePolicy.SjfPolicy;
import productsimulation.request.sourcePolicy.SourceEstimate;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.request.sourcePolicy.SourceSimplelat;

public class SetPolicyCommandIdentifier extends CommandIdentifier {
    Set<Policy> policies = new HashSet<>(Arrays.asList(
            new FIFOPolicy(), new ReadyPolicy(), new SjfPolicy(),
            new SourceEstimate(), new SourceQLen(), new SourceSimplelat()
    ));
    public SetPolicyCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    protected Command checkFits(String line) {
        Pattern pattern = Pattern.compile("^set\\s+policy\\s+(request|source)\\s+'([^']+)'\\s+on\\s+((?:'[^']+'|\\S+))\\s*$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String policyType = matcher.group(1);
            String policyName = matcher.group(2);
            String policyTarget = matcher.group(3);

            Policy policy = null;
            for (Policy p : policies) {
                if (p.getName().equals(policyName)) {
                    policy = p;
                }
            }

            if (policy == null) {
                return null;
            }

            return new SetPolicyCommand(policyType, policyTarget, policy);
        }

        return null;
    }
}
