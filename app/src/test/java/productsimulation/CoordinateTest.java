package productsimulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {

    @Test
    void testEquals() {
        Coordinate c1 = new Coordinate(1,2);
        Coordinate c2 = new Coordinate(1,2);
        Coordinate c3 = new Coordinate(1,3);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
    }

    @Test
    void testToString() {
        Coordinate c1 = new Coordinate(1,2);
        assertEquals(c1.toString(), "Coordinate{x=1, y=2}");
    }
}