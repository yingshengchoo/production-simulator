package productsimulation.command;

import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.request.Policy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

public class SetPolicyCommand extends Command {
    private final String typeField;   // "request" or "source"
    private final String target;      // "default", "*", or "'building name'"
    private final Policy policy;

    public SetPolicyCommand(String typeField, String target, Policy policy) {
        this.typeField = typeField;
        this.target = target;
        this.policy = policy;
    }

    @Override
    public void execute() {
//        System.out.println("Executing SetPolicyCommand: type=" + typeField +
//                ", policy=" + policy + ", target=" + target);
        if (!target.equals("*")) {
            State.getInstance().getBuilding(target).changePolicy(policy);
        } else {
            for (Building building : State.getInstance().getBuilding()) {
                building.changePolicy(policy);
            }
        }
    }

    public String getTypeField() {
        return typeField;
    }

    public String getTarget() {
        return target;
    }

    public String getPolicy() {
        return policy.getName();
    }
}
