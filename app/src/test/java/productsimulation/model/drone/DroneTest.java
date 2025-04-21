package productsimulation.model.drone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productsimulation.Coordinate;
import productsimulation.model.Building;
import productsimulation.request.Request;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DroneTest {
    @Mock private DronePort homePort;
    @Mock private Building source;
    @Mock private Building dest;
    @Mock private Request request;

    private Drone drone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // port at (0,0) with speed 5.0
        when(homePort.getCoordinate()).thenReturn(new Coordinate(0, 0));
        when(homePort.getDroneSpeed()).thenReturn(5);
        drone = new Drone(homePort);
    }

    @AfterEach
    void tearDown() {
        Building.buildingGlobalList.clear();
    }


    @Test
    void testAssignDeliverySetsStateToToSource() {
        drone.assignDelivery(request, source, dest);
        assertEquals(DroneState.TO_SOURCE, drone.getState());
    }

    @Test
    void testUpdateMovesTowardsSource() {
        // source at (10,0), should move 5 on x
        when(source.getCoordinate()).thenReturn(new Coordinate(10, 5));
        drone.assignDelivery(request, source, dest);
        drone.update();
        Coordinate pos = drone.getPosition();
        assertEquals(5, pos.x);
        assertEquals(0, pos.y);
        assertEquals(DroneState.TO_SOURCE, drone.getState());
        // ????
        DroneState.IDLE.toString();
        DroneState.TO_SOURCE.toString();
    }

    @Test
    void testUpdateMovesTowardsSource2() {
        // 测试转弯的情况
        when(source.getCoordinate()).thenReturn(new Coordinate(3, 5));
        drone.assignDelivery(request, source, dest);
        drone.update();
        Coordinate pos = drone.getPosition();
        assertEquals(3, pos.x);
        assertEquals(2, pos.y);
        assertEquals(DroneState.TO_SOURCE, drone.getState());
    }

    @Test
    void testUpdateWithinSpeedArrivesAndTransitions() {
        // source at (3,1)
        when(source.getCoordinate()).thenReturn(new Coordinate(3, 1));
        drone.assignDelivery(request, source, dest);
        drone.update();
        Coordinate pos = drone.getPosition();
        assertEquals(3, pos.x);
        assertEquals(1, pos.y);
        assertEquals(DroneState.TO_DEST, drone.getState());
    }

    @Test
    void testFullDeliveryCycle() {
        when(source.getCoordinate()).thenReturn(new Coordinate(3, 0));
        when(dest.getCoordinate()).thenReturn(new Coordinate(3, 4));
        when(request.getIngredient()).thenReturn("item");
        drone.assignDelivery(request, source, dest);

        // step1: to source
        drone.update();
        assertEquals(DroneState.TO_DEST, drone.getState());

        // step2: to dest
        drone.update();
        assertEquals(DroneState.RETURNING, drone.getState());

        // step3: returning
        drone.update();
        drone.update();
        assertEquals(DroneState.IDLE, drone.getState());
    }
}
