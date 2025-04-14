package productsimulation.model.road;

import productsimulation.Coordinate;
import java.io.Serializable;


public class RoadTile implements Serializable {
    private final Coordinate c;
    private final Direction direction;

    RoadTile(Coordinate c, Direction direction) {
        this.c = c;
        this.direction = direction;
    }

    public Coordinate getCoordinate() {
        return c;
    }

    public Direction getDirection() { return direction; }
}
