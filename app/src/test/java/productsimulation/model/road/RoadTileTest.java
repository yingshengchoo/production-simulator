package productsimulation.model.road;

import org.junit.jupiter.api.Test;
import productsimulation.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class RoadTileTest {

    @Test
    void getCoordinate() {
        RoadTile tile = new RoadTile(new Coordinate(1, 1));
        assertEquals(tile.getCoordinate(), new Coordinate(1, 1));
    }

    @Test
    void setDirection_invalid() {
        RoadTile tile = new RoadTile(new Coordinate(1, 1));
        tile.setDirection(null, new Coordinate(1, 1),null);
    }
}