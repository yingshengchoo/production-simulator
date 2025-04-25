package productsimulation.model.drone;

import productsimulation.Coordinate;
import productsimulation.Log;
import productsimulation.model.Building;
import productsimulation.model.waste.WasteDisposal;
import productsimulation.request.Request;
import productsimulation.request.WasteRequest;

/**
 * Drone handles transporting a single item request by flying between
 * the home port, source building, and destination building.
 */
public class Drone {

    private DroneState state = DroneState.IDLE;
    private int posX, posY;
    private final DronePort homePort;
    private Building source;
    private Building dest;
    private Request request;
    private final int speed;

    /**
     * init coordinate at port
     */
    public Drone(DronePort port) {
        this.homePort = port;
        Coordinate c = port.getCoordinate();
        this.posX = c.x;
        this.posY = c.y;
        this.speed = port.getDroneSpeed();
    }

    public DroneState getState() {
        return state;
    }

    /**
     * assign a task
     */
    public void assignDelivery(Request req, Building src, Building dst) {
        this.request = req;
        this.source = src;
        this.dest = dst;
        this.state = DroneState.TO_SOURCE;
        Log.debugLog("[Drone] Assigned to deliver "
                + req.getIngredient() + " from " + src.getName() + " to " + dst.getName());
    }

    /**
     * go one step :(
     */
    public void update() {
        if (state == DroneState.IDLE) {
            return;
        }

        Building target;
        if (state.equals(DroneState.TO_SOURCE)) {
            target = source;
        } else if (state.equals(DroneState.TO_DEST)) {
            target = dest;
        } else if (state.equals(DroneState.RETURNING)) {
            target = homePort;
        } else {
            return;
        }

        Coordinate tgt = target.getCoordinate();
        int dx = tgt.x - posX;
        int dy = tgt.y - posY;
        int manhattanDist = Math.abs(dx) + Math.abs(dy);

        // if distance < speed, get to destination.
        if (manhattanDist <= speed) {
            posX = tgt.x;
            posY = tgt.y;

            // update stage
            if (state.equals(DroneState.TO_SOURCE)) {
                state = DroneState.TO_DEST;
            } else if (state.equals(DroneState.TO_DEST)) {
                doDropoff();
                state = DroneState.RETURNING;
            } else if (state.equals(DroneState.RETURNING)) {
                Log.debugLog("[Drone] Returned to port " + homePort.getName());
                state = DroneState.IDLE;
            }
        } else {
            // first move along x-axis, then y-axis
            int remaining = speed;
            int moveX = Math.min(Math.abs(dx), remaining);
            posX += (int) (Math.signum(dx) * moveX);
            remaining -= moveX;
            int moveY = Math.min(Math.abs(dy), remaining);
            posY += (int) (Math.signum(dy) * moveY);
        }
    }


    /**
     * 在 dest 建筑中放入物品
     */
    private void doDropoff() {
        if (request instanceof WasteRequest) {
            // 提交waste
            WasteRequest wasteRequest = (WasteRequest) request;
            WasteDisposal wasteDisposal = (WasteDisposal) dest;
            wasteDisposal.commitWaste(wasteRequest.getIngredient(), wasteRequest.getCount());
        } else {
            // 提交普通request
            dest.updateStorage(request.getIngredient());
        }
        Log.debugLog("[Drone] Delivered " + request.getIngredient() + " to " + dest.getName());
    }

    public Coordinate getPosition() {
        return new Coordinate(posX, posY);
    }
}
