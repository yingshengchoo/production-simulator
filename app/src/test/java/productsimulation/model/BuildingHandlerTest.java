package productsimulation.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productsimulation.Coordinate;
import productsimulation.model.road.AtomBuilding;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BuildingHandlerTest {
    @BeforeEach
    void cleanUp() {
        Building.buildingGlobalList.clear();
    }

    @Test
    void test_getValidCoordinate() {
        // 右上
        AtomBuilding b1 = new AtomBuilding(new Coordinate(5, 5)).register();
        assertEquals(new Coordinate(10, 10), BuildingHandler.getValidCoordinate());
        // 左上
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10)).register();
        assertEquals(new Coordinate(0, 10), BuildingHandler.getValidCoordinate());
        // 右下
        AtomBuilding b3 = new AtomBuilding(new Coordinate(0, 10)).register();
        assertEquals(new Coordinate(10, 0), BuildingHandler.getValidCoordinate());
        // 左下
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 0)).register();
        assertEquals(new Coordinate(0, 0), BuildingHandler.getValidCoordinate());
    }

    @Test
    // 目前不应该找不到valid coordinate
    void test_getValidCoordinate_illegal() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(-10, -10)).register();
        assertThrows(RuntimeException.class, BuildingHandler::getValidCoordinate);
    }

    @Test
    void test_isValid_illegal() {
        assertTrue(BuildingHandler.isValid(1,1, new ArrayList<>()));
    }

    @Test
    void test_removeHandler() {
    }
}