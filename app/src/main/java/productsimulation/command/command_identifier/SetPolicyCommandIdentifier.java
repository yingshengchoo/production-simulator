package productsimulation.command.command_identifier;

import productsimulation.command.Command;
import productsimulation.command.SetPolicyCommand;

public class SetPolicyCommandIdentifier extends CommandIdentifier {
    public SetPolicyCommandIdentifier(CommandIdentifier next) {
        super(next);
    }

    @Override
    public Command checkFits(String line) {
        line = line.trim();
        if (!line.startsWith("set policy")) {
            return null;
        }

        String[] parts = line.split("\\s+", 4);
        if (parts.length < 4) {
            return null;
        }

        String policyType = parts[2];
        if (!policyType.equals("request") && !policyType.equals("source")) {
            return null;
        }

        int firstQuote = line.indexOf('\'');
        int secondQuote = line.indexOf('\'', firstQuote + 1);
        if (firstQuote == -1 || secondQuote == -1) {
            return null;
        }

        String policyName = line.substring(firstQuote + 1, secondQuote);

        int onIndex = line.indexOf(" on ", secondQuote);
        if (onIndex == -1) {
            return null;
        }

        String targetPart = line.substring(onIndex + 4).trim();

        String policyTarget;
        if (targetPart.startsWith("'") && targetPart.endsWith("'") && targetPart.length() >= 2) {
            policyTarget = targetPart.substring(1, targetPart.length() - 1);
        } else {
            policyTarget = targetPart;
        }

        return new SetPolicyCommand(policyType, policyTarget, policyName);
    }
}
