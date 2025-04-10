package productsimulation.model.road;

import org.junit.jupiter.api.Test;
import productsimulation.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class AtomBuildingTest {

    @Test
    void testEquals() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1, 1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(1, 2));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 2));
        assertEquals(b1, b1);
        assertEquals(b2, b3);
        assertNotEquals(b1, b2);
        assertNotEquals(b1, new Object());
    }

    @Test
    // nonsense, just for coverage
    void test_goOneStep() {
        AtomBuilding b = new AtomBuilding(new Coordinate(1, 1));
        assertFalse(b.goOneStep());
    }
}