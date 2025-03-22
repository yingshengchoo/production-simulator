package productsimulation.command;

public class SetPolicyCommand extends Command {
    private final String typeField;   // "request" or "source"
    private final String policy;      // "default" or "'sjf'", etc.
    private final String target;      // "default", "*", or "'building name'"

    public SetPolicyCommand(String typeField, String policy, String target) {
        this.typeField = typeField;
        this.policy = policy;
        this.target = target;
    }

    @Override
    public void execute() {
        // Implementation: set the policy in your simulation or data structure
        System.out.println("Executing SetPolicyCommand: type=" + typeField +
                ", policy=" + policy + ", target=" + target);
    }

    public String getTypeField() {
        return typeField;
    }
    public String getPolicy() {
        return policy;
    }
    public String getTarget() {
        return target;
    }
}
