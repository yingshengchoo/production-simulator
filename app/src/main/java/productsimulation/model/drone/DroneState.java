package productsimulation.model.drone;

import java.io.Serializable;

/**
 * DroneState replaces enum for representing the state of a Drone.
 */
public class DroneState implements Serializable {

    public static final DroneState IDLE = new DroneState("IDLE");
    public static final DroneState TO_SOURCE = new DroneState("TO_SOURCE");
    public static final DroneState TO_DEST = new DroneState("TO_DEST");
    public static final DroneState RETURNING = new DroneState("RETURNING");

    private final String name;

    private DroneState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DroneState that = (DroneState) obj;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}