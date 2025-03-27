package productsimulation.command;

import productsimulation.State;
import productsimulation.model.Building;
import productsimulation.request.Policy;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ReadyPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.servePolicy.SjfPolicy;
import productsimulation.request.sourcePolicy.SoleSourcePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.request.sourcePolicy.SourceSimplelat;

import java.util.*;

public class SetPolicyCommand extends Command {
    private final String typeField;   // "request" or "source"
    private final String policyTarget;      // "default", "*", or "'building name'"
    private final Policy policy;
    private final List<Building> buildings;

    public SetPolicyCommand(String typeField, String policyTarget, Policy policy) {

        this.typeField = typeField;
        this.policyTarget = policyTarget;
        this.policy = policy;
        this.buildings = new ArrayList<>();
    }

    @Override
    public void execute() {
        ServePolicy defaultServe = State.getInstance().getDefaultServePolicy();
        SourcePolicy defaultSource = State.getInstance().getDefaultSourcePolicy();
        if (policyTarget.equals("*")) {
            buildings.addAll(State.getInstance().getBuilding());
        } else if (policyTarget.equals("default")) {
            for (Building building : State.getInstance().getBuilding()) {
                if (policy instanceof SourcePolicy && building.getSourcePolicy().getName().equals(defaultSource.getName())) {
                    buildings.add(building);
                } else if (policy instanceof ServePolicy && building.getSourcePolicy().getName().equals(defaultServe.getName())) {
                    buildings.add(building);
                }
            }
        } else if  (policyTarget.startsWith("'") && policyTarget.endsWith("'")) {
            buildings.add(State.getInstance().getBuilding(policyTarget.substring(1, policyTarget.length() - 1)));
        }

        for (Building building : buildings) {
            building.changePolicy(policy);
        }

        if (policy instanceof SourcePolicy) {
            State.getInstance().setDefaultSourcePolicy(defaultSource);
        } else if (policy instanceof ServePolicy) {
            State.getInstance().setDefaultServePolicy(defaultServe);
        }


    }

    public String getTypeField() {
        return typeField;
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
