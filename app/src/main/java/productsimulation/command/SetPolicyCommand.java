package productsimulation.command;

import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.request.Policy;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ReadyPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.servePolicy.SjfPolicy;
import productsimulation.request.sourcePolicy.SourceEstimate;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.request.sourcePolicy.SourceSimplelat;

import java.util.*;

public class SetPolicyCommand extends Command {
    private final String policyType;   // "request" or "source"
    private final String policyTarget;      // "default", "*", or "'building name'"
    private Policy policy;
    private final List<Building> buildings;
    Set<Policy> availablePoliciesToSet = new HashSet<>(Arrays.asList(
            new FIFOPolicy(), new ReadyPolicy(), new SjfPolicy(),
            new SourceEstimate(), new SourceQLen(), new SourceSimplelat()
    ));

    public SetPolicyCommand(String policyType, String policyTarget, String policy) {

        this.policyType = policyType;
        this.policyTarget = policyTarget;
        for (Policy p : availablePoliciesToSet) {
            if (p.getName().equals(policy)) {
                this.policy = p;
                break;
            }
        }
        this.buildings = new ArrayList<>();
        ServePolicy defaultServe = State.getInstance().getDefaultServePolicy();
        SourcePolicy defaultSource = State.getInstance().getDefaultSourcePolicy();

        if (policyType.equals("source")) {
            if (policyTarget.equals("*")) {
                buildings.addAll(State.getInstance().getBuildings());
            } else if (policyTarget.equals("default")) {
                for (Building building : State.getInstance().getBuildings()) {
                    if (building.getSourcePolicy().getName().equals(defaultSource.getName())) {
                        buildings.add(building);
                    }
                }
            } else
            {
                buildings.add(State.getInstance().getBuildings(policyTarget));
            }
        } else {
            if (policyTarget.equals("*")) {
                buildings.addAll(State.getInstance().getBuildings());
            } else if (policyTarget.equals("default")) {
                for (Building building : State.getInstance().getBuildings()) {
                    if (building.getServePolicy().getName().equals(defaultServe.getName())) {
                        buildings.add(building);
                    }
                }
            }else {
                buildings.add(State.getInstance().getBuildings(policyTarget));
            }
        }
    }

    @Override
    public String execute() {
        if (policy == null) {
            return "The policy you want to set does not exist";
        }
        try {
            for (Building building : buildings) {
                building.changePolicy(policy);
            }

            if (policy instanceof SourcePolicy &&  policyTarget.equals("default")) {
                State.getInstance().setDefaultSourcePolicy((SourcePolicy) policy);
            } else if (policy instanceof ServePolicy &&  policyTarget.equals("default")) {
                State.getInstance().setDefaultServePolicy((ServePolicy) policy);
            }
            return null;
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
}

    public String getPolicyType() {
        return policyType;
    }

    public List<Building> getTargetBuildings() {
        return buildings;
    }

    public String getPolicyName() {
        return policy.getName();
    }

    public Policy getPolicy() {
        return policy;
    }
}
