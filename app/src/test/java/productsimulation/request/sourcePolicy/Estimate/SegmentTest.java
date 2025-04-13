package productsimulation.request.sourcePolicy.Estimate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productsimulation.Board;
import productsimulation.model.Building;
import productsimulation.model.road.Road;
import productsimulation.request.MockingEnv;

class SegmentTest {
    @BeforeEach
    public void cleanUpBefore() {
        Board.getBoard().cleanup();
        Building.buildingGlobalList.clear();
        Road.cleanup();
    }

    @Test
    void testHashCode_sameObject() {
        Segment segment = new Segment(1, mockingEnv.getBuilding());
        assertEquals(segment.hashCode(), segment.hashCode());
    }

    @Test
    void testHashCode_differentObjectsSameValues() {
        Building building = mockingEnv.getBuilding();
        Segment segment1 = new Segment(1, building);
        Segment segment2 = new Segment(1, building);
        assertEquals(segment1.hashCode(), segment2.hashCode());
    }

    @Test
    void testHashCode_differentObjectsDifferentValues() {
        Building building1 = mockingEnv.getBuildings().get(0);
        Building building2 = mockingEnv.getBuildings().get(1);
        Segment segment1 = new Segment(1, building1);
        Segment segment2 = new Segment(2, building2);
        assertNotEquals(segment1.hashCode(), segment2.hashCode());
    }

    MockingEnv mockingEnv = new MockingEnv();
    @Test
    void testEquals_sameObject() {
        Segment segment = new Segment(1, mockingEnv.getBuilding());
        assertTrue(segment.equals(segment));
    }

    @Test
    void testEquals_differentObjectWithSameValues() {
        Building building = mockingEnv.getBuilding();
        Segment segment1 = new Segment(1, building);
        Segment segment2 = new Segment(1, building);
        assertTrue(segment1.equals(segment2));
    }

    @Test
    void testEquals_differentObjectWithDifferentValues() {
        Building building1 = mockingEnv.getBuildings().get(0);
        Building building2 = mockingEnv.getBuildings().get(1);
        Segment segment1 = new Segment(1, building1);
        Segment segment2 = new Segment(2, building2);
        assertFalse(segment1.equals(segment2));
    }

    @Test
    void testEquals_nullObject() {
        Segment segment = new Segment(1, mockingEnv.getBuilding());
        assertFalse(segment.equals(null));
    }

    @Test
    void testEquals_differentClassObject() {
        Segment segment = new Segment(1, mockingEnv.getBuilding());
        Object other = "Not a segment";
        assertFalse(segment.equals(other));
    }
}