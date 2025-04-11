package productsimulation.model;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.model.road.AtomBuilding;
import productsimulation.model.road.Direction;
import productsimulation.model.road.Road;
import productsimulation.model.road.RoadTile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RoadTest {
    @BeforeEach
    public void setUp() {
        Board.cleanup();
        Road.distanceMap = new HashMap<>();
        Road.existingRoadTiles = new HashMap<>();
    }

    @Test
    void test_placeRoad() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        画一个口字型，以便测试四种方向。
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(0, 0));
        coordinates.add(new Coordinate(0, 1));
        coordinates.add(new Coordinate(0, 2));
        coordinates.add(new Coordinate(1, 2));
        coordinates.add(new Coordinate(2, 2));
        coordinates.add(new Coordinate(2, 1));
        coordinates.add(new Coordinate(2, 0));
        coordinates.add(new Coordinate(1, 0));

        Road road = new Road();
        Method placeRoadMethod = Road.class.getDeclaredMethod("placeRoad", ArrayList.class, Coordinate.class);
        placeRoadMethod.setAccessible(true);
        placeRoadMethod.invoke(road, coordinates, new Coordinate(0, 0));

        ArrayList<RoadTile> roadTiles = road.roadTiles;
        assertEquals(8, roadTiles.size());
        assertEquals(roadTiles.get(0).getDirection().getDirections(), "UP");
        assertTrue(roadTiles.get(1).getDirection().hasDirection(Direction.UP));
        assertEquals(roadTiles.get(2).getDirection().getDirections(), "RIGHT");
        assertTrue(roadTiles.get(3).getDirection().hasDirection(Direction.RIGHT));
        assertEquals(roadTiles.get(4).getDirection().getDirections(), "DOWN");
        assertTrue(roadTiles.get(5).getDirection().hasDirection(Direction.DOWN));
        assertTrue(roadTiles.get(6).getDirection().hasDirection(Direction.LEFT));
        assertEquals(roadTiles.get(6).getDirection().getDirections(), "LEFT");

        // coordinate is null
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, null, new Coordinate(0, 0)));
        // exit is null
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, null));
        // add a building on the road path
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1, 0));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(0, 0)));

        // make the path not continuous
        coordinates.clear();
        coordinates.add(new Coordinate(1, 3));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(0, 0)));
        coordinates.add(new Coordinate(2, 5));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(0, 0)));
    }

    @Test
    void test_generateRoad_new() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 3));
        Road road = new Road(b1, b2);
        assertEquals(1, Road.distanceMap.size());
        ArrayList<RoadTile> roadTiles = road.roadTiles;
        assertEquals(17, roadTiles.size());
        assertEquals(17, Road.existingRoadTiles.size());
        Road road2 = new Road(b1, b3);
        assertEquals(2, Road.distanceMap.size());
        assertEquals(1, road2.roadTiles.size());
        assertEquals(18, Road.existingRoadTiles.size());
    }

    @Test
    void test_generateRoad_special() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b2_1 = new AtomBuilding(new Coordinate(10, 11));
//        起点和终点相同，不应该生成新路
        Road road1 = new Road(b1, b1);
        assertEquals(0, Road.distanceMap.size());
        Road road2 = new Road(b1, b2);
//        同起点同终点已有路，不应该生成新路
        Road road3 = new Road(b1, b2);
//        两个建筑相邻，distanceMap中增加item，但不应该生成新路
        Road road4 = new Road(b2, b2_1);
        assertEquals(2, Road.distanceMap.size());
        assertEquals(17, Road.existingRoadTiles.size());
//        起点和终点对调，理应生成新路，因为道路有向。
        Road road5 = new Road(b2, b1);
        assertEquals(3, Road.distanceMap.size());
        assertEquals(31, Road.existingRoadTiles.size());
    }

    @Test
//    测building中转
    void test_generateRoad_reuse1() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        Road road1 = new Road(b3, b2);
//        确认路已修建，且全局可见
        assertEquals(1, Road.distanceMap.size());
//        确认路线正确
        ArrayList<RoadTile> roadTiles = road1.roadTiles;
        assertEquals(8, roadTiles.size());
        assertEquals(8, Road.existingRoadTiles.size());
        Road road2 = new Road(b1, b2);
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        assertEquals(17, roadTiles2.size());
        assertEquals(17, Road.existingRoadTiles.size());
    }

    @Test
//    测垂线段绘制
    void test_generateRoad_reuse2() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(5,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 3));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 3));
        Road road1 = new Road(b2, b3);
//        确认路已修建，且全局可见
        assertEquals(1, Road.distanceMap.size());
        Road road2 = new Road(b1, b3);
        assertEquals(2, Road.distanceMap.size());
        Road road3 = new Road(b1, b2);
        assertEquals(3, Road.distanceMap.size());

        //        确认路线正确
        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2= road2.roadTiles;
        ArrayList<RoadTile> roadTiles3 = road3.roadTiles;
        assertEquals(8, roadTiles1.size());
        assertEquals(5, roadTiles2.size());
        assertEquals(6, roadTiles3.size());
        assertEquals(13, Road.existingRoadTiles.size());
    }

    @Test
//    测放弃reuse
    void test_generateRoad_reuse3() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(20, 20));
        Road road1 = new Road(b3, b2);
//        确认路已修建，且全局可见
        assertEquals(1, Road.distanceMap.size());
        Road road2 = new Road(b1, b2);
        assertEquals(2, Road.distanceMap.size());

        //        确认路线正确
        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        assertEquals(19, roadTiles1.size());
        assertEquals(17, roadTiles2.size());
        assertEquals(36, Road.existingRoadTiles.size());
    }

    @Test
//    测1 对 n
    void test_generateRoad_reuse4() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,10));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 20));
        Road road1 = new Road(b1, b2);
        Road road2 = new Road(b1, b3);
        Road road3 = new Road(b1, b4);
//        确认路已修建，且全局可见
        assertEquals(3, Road.distanceMap.size());
//        确认路线正确
        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        ArrayList<RoadTile> roadTiles3 = road3.roadTiles;
        assertEquals(8, roadTiles1.size());
        assertEquals(17, roadTiles2.size());
        assertEquals(18, roadTiles3.size());
        assertEquals(27, Road.existingRoadTiles.size());
    }

    @Test
//    测n 对 1
    void test_generateRoad_reuse5() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(10,10));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(1, 1));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(1, 20));
        Road road1 = new Road(b2, b1);
        assertEquals(1, Road.distanceMap.size());
        Road road2 = new Road(b3, b1);
        assertEquals(2, Road.distanceMap.size());
        Road road3 = new Road(b4, b1);
        assertEquals(3, Road.distanceMap.size());

        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        ArrayList<RoadTile> roadTiles3 = road3.roadTiles;
        assertEquals(17, roadTiles1.size());
        assertEquals(8, roadTiles2.size());
        assertEquals(18, roadTiles3.size());
        assertEquals(32, Road.existingRoadTiles.size());
    }

    @Test
//    测对无关路线的复用
    void test_generateRoad_reuse6() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(20, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 10));
        Road road1 = new Road(b3, b4);
        assertEquals(1, Road.distanceMap.size());
        Road road2 = new Road(b1, b2);
        assertEquals(2, Road.distanceMap.size());

        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        assertEquals(8, roadTiles1.size());
        assertEquals(27, roadTiles2.size());
        assertEquals(27, Road.existingRoadTiles.size());
    }

    @Test
//    和reuse6一样，但不应该复用，因为道路的方向跟刚刚相反
    void test_generateRoad_reuse6_reverse() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(20, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 10));
        Road road1 = new Road(b4, b3);
        assertEquals(1, Road.distanceMap.size());
        Road road2 = new Road(b1, b2);
        assertEquals(2, Road.distanceMap.size());

        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        assertEquals(8, roadTiles1.size());
        assertEquals(27, roadTiles2.size());
        assertEquals(34, Road.existingRoadTiles.size());
    }

    @Test
//    测对无关路线的复用
    void test_generateRoad_reuse7() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 10));
        Road road1 = new Road(b3, b4);
        assertEquals(1, Road.distanceMap.size());

        AtomBuilding block1 = new AtomBuilding(new Coordinate(5, 0));
        AtomBuilding block2 = new AtomBuilding(new Coordinate(5, 1));
        AtomBuilding block3 = new AtomBuilding(new Coordinate(5, 2));
        AtomBuilding block4 = new AtomBuilding(new Coordinate(5, 3));
        AtomBuilding block5 = new AtomBuilding(new Coordinate(5, 4));
        AtomBuilding block6 = new AtomBuilding(new Coordinate(5, 5));

        Road road2 = new Road(b1, b2);
        assertEquals(2, Road.distanceMap.size());

        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        assertEquals(8, roadTiles1.size());
        assertEquals(18, roadTiles2.size());
        assertEquals(26, Road.existingRoadTiles.size());
    }

    @Test
//    @Disabled("waiting for Building debug")
//    测对无关路线的复用
    void test_generateRoad_reuse8() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 1));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(10, 10));
        Road road1 = new Road(b3, b4);
        assertEquals(1, Road.distanceMap.size());

        AtomBuilding block1 = new AtomBuilding(new Coordinate(5, 0));
        AtomBuilding block2 = new AtomBuilding(new Coordinate(5, 1));
        AtomBuilding block3 = new AtomBuilding(new Coordinate(5, 2));
        AtomBuilding block4 = new AtomBuilding(new Coordinate(5, 3));
        AtomBuilding block5 = new AtomBuilding(new Coordinate(5, 4));
        AtomBuilding block6 = new AtomBuilding(new Coordinate(5, 5));
        AtomBuilding block7 = new AtomBuilding(new Coordinate(5, 6));
        AtomBuilding block8 = new AtomBuilding(new Coordinate(5, 7));
        AtomBuilding block9 = new AtomBuilding(new Coordinate(5, 8));

        Road road2 = new Road(b1, b2);
        assertEquals(2, Road.distanceMap.size());

        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        assertEquals(8, roadTiles1.size());
        assertEquals(26, roadTiles2.size());
        assertEquals(26, Road.existingRoadTiles.size());
    }

    @Test
    void test_generateRoad_unreachable() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));

        AtomBuilding b3 = new AtomBuilding(new Coordinate(3, 0));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(3, 1));
        AtomBuilding b5 = new AtomBuilding(new Coordinate(3, 2));
        AtomBuilding b6 = new AtomBuilding(new Coordinate(3, 3));
        AtomBuilding b7 = new AtomBuilding(new Coordinate(2, 3));
        AtomBuilding b8 = new AtomBuilding(new Coordinate(1, 3));
        AtomBuilding b9 = new AtomBuilding(new Coordinate(0, 3));
        Road road = new Road(b1, b2);
//        确认路并未修建
        assertEquals(1, Road.distanceMap.size());
        assertEquals(0, Road.existingRoadTiles.size());
    }

    @Test
    void test_generateRoad_unreachable2() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b = new AtomBuilding(new Coordinate(15, 10));
        Road road1 = new Road(b, b2);
        assertEquals(1, Road.distanceMap.size());

        AtomBuilding b3 = new AtomBuilding(new Coordinate(3, 0));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(3, 1));
        AtomBuilding b5 = new AtomBuilding(new Coordinate(3, 2));
        AtomBuilding b6 = new AtomBuilding(new Coordinate(3, 3));
        AtomBuilding b7 = new AtomBuilding(new Coordinate(2, 3));
        AtomBuilding b8 = new AtomBuilding(new Coordinate(1, 3));
        AtomBuilding b9 = new AtomBuilding(new Coordinate(0, 3));
        Road road2 = new Road(b1, b2);
        assertEquals(2, Road.distanceMap.size());
        assertEquals(4, Road.existingRoadTiles.size());
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
}