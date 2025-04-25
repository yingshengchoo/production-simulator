package productsimulation.model.drone;

import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.model.Building;
import productsimulation.model.BuildingType;
import productsimulation.request.Request;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DronePort represents a building that can construct and dispatch drones
 * to move items between in-range buildings.
 */
public class DronePort extends Building implements Serializable {
    private static final int MAX_CAPACITY = 10;
    private static final int RADIUS       = 20;
    private static final int SPEED        = 5; // tiles per timestep

    private final List<Drone> drones = new ArrayList<>();

    public DronePort(String name,
                     BuildingType type,
                     SourcePolicy sourcePolicy,
                     ServePolicy servePolicy,
                     Coordinate coordinate) {
        super(name, type,
                Collections.emptyList(),
                sourcePolicy,
                servePolicy,
                coordinate);
    }

    /**
     * Find any port that can dispatch a drone for this request.
     */
    public static DronePort findEligiblePort(Request req, Building src, Building dst) {
        for (Building b : buildingGlobalList) {
            if (b instanceof DronePort port && port.tryDispatch(req, src, dst)) {
                return port;
            }
        }
        return null;
    }

    /** Register this port as a building and block its tile on the board. */
    public DronePort register() {
        buildingGlobalList.add(this);
        Board.getBoard().addBuilding(this);
        return this;
    }

    /** Construct one drone here (up to MAX_CAPACITY). */
    public boolean constructDrone() {
        if (drones.size() < MAX_CAPACITY) {
            drones.add(new Drone(this));
            Log.debugLog("[DronePort] Constructed new drone at " + name);
            return true;
        }
        Log.debugLog("[DronePort] Capacity full at " + name);
        return false;
    }

    public boolean hasIdleDrone() {
        return drones.stream().anyMatch(d -> d.getState() == DroneState.IDLE);
    }

    public Drone getIdleDrone() {
        return drones.stream()
                .filter(d -> d.getState() == DroneState.IDLE)
                .findFirst().orElse(null);
    }

    /** Manhattan-distance ≤ RADIUS → in range. */
    public boolean inRange(Building b) {
        Coordinate c = b.getCoordinate();
        int dist = Math.abs(c.x - coordinate.x) + Math.abs(c.y - coordinate.y);
        return dist <= RADIUS;
    }

    /**
     * Try to hand off this request to an idle drone if both source & dest are in range.
     */
    public boolean tryDispatch(Request req, Building src, Building dst) {
        if (inRange(src) && inRange(dst) && hasIdleDrone()) {
            getIdleDrone().assignDelivery(req, src, dst);
            return true;
        }
        return false;
    }

    public boolean isReachable(Building src, Building dst) {
        return inRange(src) && inRange(dst);
    }

    /** Move all drones one step each logic tick. */
    @Override
    public boolean goOneStep() {
        drones.forEach(Drone::update);
        return true;
    }

    public int getDroneSpeed() {
        return SPEED;
    }

    /** @return an unmodifiable view of the drones stationed here */
    public List<Drone> getDrones() {
        return Collections.unmodifiableList(drones);
    }
}
