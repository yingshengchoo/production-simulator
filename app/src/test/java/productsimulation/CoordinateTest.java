package productsimulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {
    @Test
    void testIsNeighbor() {
        Coordinate c1 = new Coordinate(1,2);
        Coordinate c2 = new Coordinate(1,2);
        assertFalse(Coordinate.isNeighbor(c2, c1));
        Coordinate c3 = new Coordinate(1,3);
        assertTrue(Coordinate.isNeighbor(c2, c3));
    }

    @Test
    void testEquals() {
        Coordinate c1 = new Coordinate(1,2);
        Coordinate c2 = new Coordinate(1,2);
        Coordinate c3 = new Coordinate(1,3);
        assertEquals(c1, c1);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, new Object());
    }

    @Test
    void testToString() {
        Coordinate c1 = new Coordinate(1,2);
        assertEquals(c1.toString(), "Coordinate{x=1, y=2}");
    }
}