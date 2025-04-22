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
public class DronePort extends Building {
    private static final int MAX_CAPACITY = 10;
    private static final int RADIUS = 20;
    private static final int SPEED = 5; // tiles per timestep
    private final List<Drone> drones = new ArrayList<>();

    /**
     *
     * @param name
     * @param type       BuildingType.DRONE_PORT
     * @param sourcePolicy 请选择来源策略（传null）
     * @param servePolicy  请选择服务策略（传null）
     * @param coordinate
     */
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
     * find if there is an eligible port around src building
     * if there is, assign the task to an idle drone :)
     * @param src
     * @param dst
     * @return
     */
    public static DronePort findEligiblePort(Request req, Building src, Building dst) {
        for (Building b : buildingGlobalList) {
            if (b instanceof DronePort port) {
                if (port.tryDispatch(req, src, dst)) {
                    return port;
                }
            }
        }
        return null;
    }

    /**
     * add to globalList.
     */
    public DronePort register() {
        buildingGlobalList.add(this);
        Board.getBoard().addBuilding(this);
        return this;
    }

    /**
     * add one drone if not exceed the capacity.
     */
    public boolean constructDrone() {
        if (drones.size() < MAX_CAPACITY) {
            Drone drone = new Drone(this);
            drones.add(drone);
            Log.debugLog("[DronePort] Constructed new drone at " + name);
            return true;
        } else {
            Log.debugLog("[DronePort] Capacity full, cannot construct more drones at " + name);
            return false;
        }
    }


    public boolean hasIdleDrone() {
        return drones.stream().anyMatch(d -> d.getState() == DroneState.IDLE);
    }


    public Drone getIdleDrone() {
        return drones.stream().filter(d -> d.getState() == DroneState.IDLE)
                .findFirst().orElse(null);
    }

    /**
     * check if building b is in range of this drone port.
     */
    public boolean inRange(Building b) {
        Coordinate c = b.getCoordinate();
        int dist = Math.abs(c.x - coordinate.x) + Math.abs(c.y - coordinate.y);
        return dist <= RADIUS;
    }

    /*
    try dispatch request to an idle drone
    if no idle drone exist, return false
     */
    public boolean tryDispatch(Request req, Building src, Building dst) {
        if (inRange(src) && inRange(dst) && hasIdleDrone()) {
            Drone drone = getIdleDrone();
            drone.assignDelivery(req, src, dst);
            return true;
        }
        return false;
    }

    @Override
    public boolean goOneStep() {
        for (Drone drone : drones) {
            drone.update();
        }
        return true;
    }


    public int getDroneSpeed() {
        return SPEED;
    }
}

