package productsimulation.model.road;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.model.Building;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoadTest {
    @BeforeEach
    public void setUp() {
        Board.getBoard().cleanup();
        Road.cleanup();
    }

    @Test
    void test_placeRoad() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
//        画一个口字型，以便测试四种方向。
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(0, 0));
        coordinates.add(new Coordinate(0, 1));
        coordinates.add(new Coordinate(0, 2));
        coordinates.add(new Coordinate(0, 3));

        coordinates.add(new Coordinate(1, 3));
        coordinates.add(new Coordinate(2, 3));
        coordinates.add(new Coordinate(3, 3));
        coordinates.add(new Coordinate(4, 3));

        coordinates.add(new Coordinate(4, 2));
        coordinates.add(new Coordinate(4, 1));
        coordinates.add(new Coordinate(3, 1));

        Class<?> clazz = Road.class;
        Constructor<?> constructor = clazz.getDeclaredConstructor(Building.class, Building.class, int.class);
        constructor.setAccessible(true);
        Object instance = constructor.newInstance(new AtomBuilding(new Coordinate(1, 0)), new AtomBuilding(new Coordinate(2, 1)), 11);
        Road road = (Road)instance;

        Method placeRoadMethod = Road.class.getDeclaredMethod("placeRoad", ArrayList.class, Coordinate.class, Coordinate.class);
        placeRoadMethod.setAccessible(true);
        placeRoadMethod.invoke(instance, coordinates,  new Coordinate(1, 0), new Coordinate(2, 1));

        List<RoadTile> roadTiles = road.getRoadTiles();
        assertEquals(11, roadTiles.size());

        assertEquals(roadTiles.get(0).getFromDirection().getDirections(), "RIGHT");
        assertTrue(roadTiles.get(0).getToDirection().hasDirection(Direction.DOWN));
        assertEquals(roadTiles.get(1).getFromDirection().getDirections(), "UP");
        assertTrue(roadTiles.get(1).getToDirection().hasDirection(Direction.DOWN));
        assertEquals(roadTiles.get(2).getFromDirection().getDirections(), "UP");
        assertTrue(roadTiles.get(2).getToDirection().hasDirection(Direction.DOWN));
        assertEquals(roadTiles.get(3).getFromDirection().getDirections(), "UP");
        assertTrue(roadTiles.get(3).getToDirection().hasDirection(Direction.RIGHT));

        assertEquals(roadTiles.get(4).getFromDirection().getDirections(), "LEFT");
        assertTrue(roadTiles.get(4).getToDirection().hasDirection(Direction.RIGHT));
        assertEquals(roadTiles.get(5).getFromDirection().getDirections(), "LEFT");
        assertTrue(roadTiles.get(5).getToDirection().hasDirection(Direction.RIGHT));
        assertEquals(roadTiles.get(6).getFromDirection().getDirections(), "LEFT");
        assertTrue(roadTiles.get(6).getToDirection().hasDirection(Direction.RIGHT));
        assertEquals(roadTiles.get(7).getFromDirection().getDirections(), "LEFT");
        assertTrue(roadTiles.get(7).getToDirection().hasDirection(Direction.UP));

        assertEquals(roadTiles.get(8).getFromDirection().getDirections(), "DOWN");
        assertTrue(roadTiles.get(8).getToDirection().hasDirection(Direction.UP));
        assertEquals(roadTiles.get(9).getFromDirection().getDirections(), "DOWN");
        assertTrue(roadTiles.get(9).getToDirection().hasDirection(Direction.LEFT));
        assertEquals(roadTiles.get(10).getFromDirection().getDirections(), "RIGHT");
        assertTrue(roadTiles.get(10).getToDirection().hasDirection(Direction.LEFT));

        // coordinate is null
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, null, new Coordinate(1, 0), new Coordinate(2, 1)));
        // exit is null
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(1, 0), null));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, null, new Coordinate(2, 1)));
        // add a building on the road path
        AtomBuilding b1 = new AtomBuilding(new Coordinate(0, 1));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(1, 0), new Coordinate(2, 1)));

        // make the path not continuous
        coordinates.clear();
        coordinates.add(new Coordinate(1, 3));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(1, 0), new Coordinate(2, 1)));
        coordinates.add(new Coordinate(2, 5));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(1, 0), new Coordinate(2, 1)));
    }

    @Test
    void test_generateRoad_new() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 3));
        Road road = Road.generateRoad(b1, b2);
        assertNotNull(road);
        assertEquals(1, Road.roadMap.size());
        List<RoadTile> roadTiles = road.getRoadTiles();
        assertEquals(17, roadTiles.size());
        assertEquals(17, Road.existingRoadTiles.size());
        Road road2 = Road.generateRoad(b1, b3);
        assertNotNull(road2);
        assertEquals(2, Road.roadMap.size());
        assertEquals(1, road2.getRoadTiles().size());
        assertEquals(18, Road.existingRoadTiles.size());
    }

    @Test
    void test_generateRoad_special() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b2_1 = new AtomBuilding(new Coordinate(10, 11));

//        起点和终点相同，不应该生成新路
        Road road1 = Road.generateRoad(b1, b1);
        assertEquals(road1, Road.PORTAL);
//        同起点同终点已有路，不应该生成新路
        Road road2 = Road.generateRoad(b1, b2);
        Road road3 = Road.generateRoad(b1, b2);
        assertEquals(road2, road3);
//        两个建筑相邻，不应该生成新路
        Road road4 = Road.generateRoad(b2, b2_1);
        assertEquals(road4, Road.PORTAL);
        assertEquals(17, Road.existingRoadTiles.size());
//        起点和终点对调，理应生成新路，因为道路有向。
        Road road5 = Road.generateRoad(b2, b1);
        assertNotEquals(road2, road5);
        assertEquals(2, Road.roadMap.size());
        assertEquals(32, Road.existingRoadTiles.size());
    }

    @Test
//    测building中转
    void test_generateRoad_reuse1() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));

        Road road1 = Road.generateRoad(b3, b2);
        assertNotNull(road1);
        assertEquals(1, Road.roadMap.size());
        List<RoadTile> roadTiles = road1.getRoadTiles();
        assertEquals(8, roadTiles.size());
        assertEquals(8, Road.existingRoadTiles.size());

        Road road2 = Road.generateRoad(b1, b2);
        assertNotNull(road2);
        List<RoadTile> roadTiles2 = road2.getRoadTiles();
        assertEquals(17, roadTiles2.size());
        assertEquals(17, Road.existingRoadTiles.size());

        assertEquals(2, Road.roadMap.size());
    }

    @Test
//    测垂线段绘制
    void test_generateRoad_reuse2() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(5,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 3));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 3));
        Road road1 = Road.generateRoad(b2, b3);
        Road road2 = Road.generateRoad(b1, b3);
        Road road3 = Road.generateRoad(b1, b2);
        assertNotNull(road1);
        assertNotNull(road2);
        assertNotNull(road3);

        List<RoadTile> roadTiles1 = road1.getRoadTiles();
        List<RoadTile> roadTiles2= road2.getRoadTiles();
        List<RoadTile> roadTiles3 = road3.getRoadTiles();
        assertEquals(8, roadTiles1.size());
        assertEquals(5, roadTiles2.size());
        assertEquals(6, roadTiles3.size());
        assertEquals(13, Road.existingRoadTiles.size());

        assertEquals(3, Road.roadMap.size());
    }

    @Test
//    测放弃reuse
    void test_generateRoad_reuse3() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(20, 20));
        Road road1 = Road.generateRoad(b3, b2);
        Road road2 = Road.generateRoad(b1, b2);
        assertNotNull(road1);
        assertNotNull(road2);
        List<RoadTile> roadTiles1 = road1.getRoadTiles();
        List<RoadTile> roadTiles2 = road2.getRoadTiles();
        assertEquals(19, roadTiles1.size());
        assertEquals(17, roadTiles2.size());
        assertEquals(36, Road.existingRoadTiles.size());

        assertEquals(2, Road.roadMap.size());
    }

    @Test
//    测1 对 n
    void test_generateRoad_reuse4() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,10));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 20));
        Road road1 = Road.generateRoad(b1, b2);
        Road road2 = Road.generateRoad(b1, b3);
        Road road3 = Road.generateRoad(b1, b4);
        assertNotNull(road1);
        assertNotNull(road2);
        assertNotNull(road3);
        List<RoadTile> roadTiles1 = road1.getRoadTiles();
        List<RoadTile> roadTiles2 = road2.getRoadTiles();
        List<RoadTile> roadTiles3 = road3.getRoadTiles();
        assertEquals(8, roadTiles1.size());
        assertEquals(17, roadTiles2.size());
        assertEquals(18, roadTiles3.size());
        assertEquals(27, Road.existingRoadTiles.size());

        assertEquals(3, Road.roadMap.size());
    }

    @Test
//    测n 对 1
    void test_generateRoad_reuse5() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(10,10));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(1, 1));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(1, 20));
        Road road1 = Road.generateRoad(b2, b1);
        Road road2 = Road.generateRoad(b3, b1);
        Road road3 = Road.generateRoad(b4, b1);
        assertNotNull(road1);
        assertNotNull(road2);
        assertNotNull(road3);
        List<RoadTile> roadTiles1 = road1.getRoadTiles();
        List<RoadTile> roadTiles2 = road2.getRoadTiles();
        List<RoadTile> roadTiles3 = road3.getRoadTiles();
        assertEquals(17, roadTiles1.size());
        assertEquals(8, roadTiles2.size());
        assertEquals(18, roadTiles3.size());
        assertEquals(32, Road.existingRoadTiles.size());

        assertEquals(3, Road.roadMap.size());
    }

    @Test
//    测对无关路线的复用
    void test_generateRoad_reuse6() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(20, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 10));
        Road road1 = Road.generateRoad(b3, b4);
        Road road2 = Road.generateRoad(b1, b2);
        assertNotNull(road1);
        assertNotNull(road2);
        List<RoadTile> roadTiles1 = road1.getRoadTiles();
        List<RoadTile> roadTiles2 = road2.getRoadTiles();
        assertEquals(8, roadTiles1.size());
        assertEquals(27, roadTiles2.size());
        assertEquals(27, Road.existingRoadTiles.size());

        assertEquals(2, Road.roadMap.size());
    }

    @Test
//    和reuse6一样，但不应该复用，因为道路的方向跟刚刚相反
    void test_generateRoad_reuse6_reverse() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(20, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 10));
        Road road1 = Road.generateRoad(b4, b3);
        Road road2 = Road.generateRoad(b1, b2);
        assertNotNull(road1);
        assertNotNull(road2);
        List<RoadTile> roadTiles1 = road1.getRoadTiles();
        List<RoadTile> roadTiles2 = road2.getRoadTiles();
        assertEquals(8, roadTiles1.size());
        assertEquals(27, roadTiles2.size());
        assertEquals(34, Road.existingRoadTiles.size());

        assertEquals(2, Road.roadMap.size());
    }

    @Test
//    测对无关路线的复用
    void test_generateRoad_reuse7() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 10));

        Road road1 = Road.generateRoad(b3, b4);
        assertNotNull(road1);

        AtomBuilding block1 = new AtomBuilding(new Coordinate(5, 0));
        AtomBuilding block2 = new AtomBuilding(new Coordinate(5, 1));
        AtomBuilding block3 = new AtomBuilding(new Coordinate(5, 2));
        AtomBuilding block4 = new AtomBuilding(new Coordinate(5, 3));
        AtomBuilding block5 = new AtomBuilding(new Coordinate(5, 4));
        AtomBuilding block6 = new AtomBuilding(new Coordinate(5, 5));

        Road road2 = Road.generateRoad(b1, b2);
        assertNotNull(road2);

        List<RoadTile> roadTiles1 = road1.getRoadTiles();
        List<RoadTile> roadTiles2 = road2.getRoadTiles();
        assertEquals(8, roadTiles1.size());
        assertEquals(18, roadTiles2.size());
        assertEquals(26, Road.existingRoadTiles.size());

        assertEquals(2, Road.roadMap.size());
    }

    @Test
//    测对无关路线的复用
    void test_generateRoad_reuse8() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 10));
        Road road1 = Road.generateRoad(b3, b4);
        assertNotNull(road1);

        AtomBuilding block1 = new AtomBuilding(new Coordinate(5, 0));
        AtomBuilding block2 = new AtomBuilding(new Coordinate(5, 1));
        AtomBuilding block3 = new AtomBuilding(new Coordinate(5, 2));
        AtomBuilding block4 = new AtomBuilding(new Coordinate(5, 3));
        AtomBuilding block5 = new AtomBuilding(new Coordinate(5, 4));
        AtomBuilding block6 = new AtomBuilding(new Coordinate(5, 5));
        AtomBuilding block7 = new AtomBuilding(new Coordinate(5, 6));
        AtomBuilding block8 = new AtomBuilding(new Coordinate(5, 7));
        AtomBuilding block9 = new AtomBuilding(new Coordinate(5, 8));

        Road road2 = Road.generateRoad(b1, b2);
        assertNotNull(road2);

        List<RoadTile> roadTiles1 = road1.getRoadTiles();
        List<RoadTile> roadTiles2 = road2.getRoadTiles();
        assertEquals(8, roadTiles1.size());
        assertEquals(26, roadTiles2.size());
        assertEquals(26, Road.existingRoadTiles.size());

        assertEquals(2, Road.roadMap.size());
    }

    @Test
    void test_generateRoad_unreachable() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b = new AtomBuilding(new Coordinate(15, 10));
        Road road1 = Road.generateRoad(b, b2);
        assertNotNull(road1);

        AtomBuilding b3 = new AtomBuilding(new Coordinate(3, 0));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(3, 1));
        AtomBuilding b5 = new AtomBuilding(new Coordinate(3, 2));
        AtomBuilding b6 = new AtomBuilding(new Coordinate(3, 3));
        AtomBuilding b7 = new AtomBuilding(new Coordinate(2, 3));
        AtomBuilding b8 = new AtomBuilding(new Coordinate(1, 3));
        AtomBuilding b9 = new AtomBuilding(new Coordinate(0, 3));
        Road road2 = Road.generateRoad(b1, b2);
        assertNull(road2);

        assertEquals(4, Road.existingRoadTiles.size());

        assertEquals(1, Road.roadMap.size());
    }

    @Test
    void test_shortestPath() {
        Coordinate c1 = new Coordinate(0, 0);
        Coordinate c2 = new Coordinate(5, 2);
        ArrayList<Coordinate> p1 = new ArrayList<>();
//        此处的distance并非曼哈顿距离，而是带权距离。
        assertEquals(12, Road.shortestPath(c1, c2, p1));
//        System.out.println(p1);
        Board.getBoard().setBoardPosWeight(new Coordinate(1, 0), Integer.MAX_VALUE);
        Board.getBoard().setBoardPosWeight(new Coordinate(1, 1), Integer.MAX_VALUE);
        Board.getBoard().setBoardPosWeight(new Coordinate(1, 2), Integer.MAX_VALUE);
        Board.getBoard().setBoardPosWeight(new Coordinate(1, 3), Integer.MAX_VALUE);
        ArrayList<Coordinate> p2 = new ArrayList<>();
        assertEquals(20, Road.shortestPath(c1, c2, p2));
//        System.out.println(p2);
        Board.getBoard().setBoardPosWeight(new Coordinate(0, 3), Integer.MAX_VALUE);
        ArrayList<Coordinate> p3 = new ArrayList<>();
        assertEquals(-1, Road.shortestPath(c1, c2, p3));
    }

    @Test
    void test_connectHandler_illegal() {
        assertNotNull(Road.connectHandler(null, "name"));
        assertNotNull(Road.connectHandler("name", null));
        assertNotNull(Road.connectHandler("name1", "name2"));
    }

    @Test
    void test_getDistance_special() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(1,2));
        assertEquals(0, Road.getDistance(b1, b2));
        assertEquals(0, Road.getDistance(b1, b1));
    }

    @Test
    void test_getDistance_illegal() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(3,2));
        assertThrows(IllegalArgumentException.class, ()->Road.getDistance(b1, b2));
    }
}