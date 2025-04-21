package productsimulation.model.drone;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productsimulation.Coordinate;
import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.Request;

import static org.mockito.Mockito.*;

class DronePortTest {
    @Mock private Building src;
    @Mock private Building dst;
    @Mock private Request req;

    private DronePort port;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Building.buildingGlobalList.clear();
        port = new DronePort("dp", null, null, null, new Coordinate(0, 0));
        //  src, dst at (1,1)，(2,2)
        when(src.getCoordinate()).thenReturn(new Coordinate(1, 1));
        when(dst.getCoordinate()).thenReturn(new Coordinate(2, 2));
    }

    @AfterEach
    void tearDown() {
        Building.buildingGlobalList.clear();
    }

    @Test
    void testInRange() {
        assertTrue(port.inRange(src));
        Building far = mock(Building.class);
        when(far.getCoordinate()).thenReturn(new Coordinate(100, 100));
        assertFalse(port.inRange(far));
    }

    @Test
    void testHasIdleDroneInitiallyFalse() {
        assertFalse(port.hasIdleDrone());
    }

    @Test
    void testConstructDroneCreatesIdle() {
        port.constructDrone();
        assertTrue(port.hasIdleDrone());
        Drone d = port.getIdleDrone();
        assertNotNull(d);

        // new drone should in idle state
        assertEquals(DroneState.IDLE, d.getState());

        for (int i = 0; i < 10; i++) {
            port.constructDrone();
            if (i == 9) {
                assertFalse(port.constructDrone());
            }
        }
    }

    @Test
    void testTryDispatchNoDrone() {
        // no drone should be false
        assertFalse(port.tryDispatch(req, src, dst));
    }

    @Test
    void testTryDispatchOutOfRange() {
        port.constructDrone();

        when(dst.getCoordinate()).thenReturn(new Coordinate(100, 100));
        assertFalse(port.tryDispatch(req, src, dst));

        // should have one idle drone after fail
        assertTrue(port.hasIdleDrone());
    }

    @Test
    void testTryDispatchSuccess() {
        port.constructDrone();
        Drone d = port.getIdleDrone();
        boolean dispatched = port.tryDispatch(req, src, dst);
        assertTrue(dispatched);
        assertEquals(DroneState.TO_SOURCE, d.getState());
        // should have no idle drone now.
        assertFalse(port.hasIdleDrone());
    }

    @Test
    void testGoOneStepDoesNotThrow() {
        port.constructDrone();
        // :)
        for (int i = 0; i < 3; i++) {
            assertTrue(port.goOneStep());
        }
    }

    @Test
    void testFindEligiblePortReturnsNullWhenNone() {
        assertNull(DronePort.findEligiblePort(null, src, dst), "无可用港口时应返回 null");
    }

    @Test
    void testFindEligiblePortFindsAvailablePort() {
        port.register();
        port.constructDrone();
        Request req = mock(Request.class);
        Recipe recipe = mock(Recipe.class);
        when(recipe.getIngredients()).thenReturn(null);
        when(req.getRecipe()).thenReturn(recipe);
        DronePort result = DronePort.findEligiblePort(req, src, dst);
        assertSame(port, result);
    }
}
