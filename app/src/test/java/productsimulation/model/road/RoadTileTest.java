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
}