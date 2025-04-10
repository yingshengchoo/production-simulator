package productsimulation.model;

import org.junit.jupiter.api.BeforeEach;
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
    // todo 把手动调试换成自动assert，正确的道路只需满足以下条件：1.总长度符合预期 2.相邻的坐标相邻(已由placeRoad保证) 3.起点和起点建筑相邻，终点和终点建筑相邻
    // 封装为一个函数，入参为road和预期长度
    @BeforeEach
    public void setUp() {
        Road.board = new Board();
        Road.existingRoads = new HashMap<>();
        Road.existingRoadTiles = new HashMap<>();
    }

    private int getTotalRoads() {
        int roadCount = 0;
        for (Map.Entry<Building, HashMap<Building, Road>> entry : Road.existingRoads.entrySet()) {
            HashMap<Building, Road> subMap = entry.getValue();
            roadCount += subMap.size();
        }
        return roadCount;
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
        Method placeRoadMethod = Road.class.getDeclaredMethod("placeRoad", ArrayList.class, Coordinate.class, boolean.class);
        placeRoadMethod.setAccessible(true);
        placeRoadMethod.invoke(road, coordinates, new Coordinate(0, 0), false);

        ArrayList<RoadTile> roadTiles = road.roadTiles;
        assertEquals(8, roadTiles.size());
        assertTrue(roadTiles.get(0).getIsEnd());
        assertEquals(roadTiles.get(0).getDirection().getDirections(), "UP");
        assertTrue(roadTiles.get(1).getDirection().hasDirection(Direction.UP));
        assertEquals(roadTiles.get(2).getDirection().getDirections(), "RIGHT");
        assertTrue(roadTiles.get(3).getDirection().hasDirection(Direction.RIGHT));
        assertEquals(roadTiles.get(4).getDirection().getDirections(), "DOWN");
        assertTrue(roadTiles.get(5).getDirection().hasDirection(Direction.DOWN));
        assertTrue(roadTiles.get(6).getDirection().hasDirection(Direction.LEFT));
        assertEquals(roadTiles.get(6).getDirection().getDirections(), "LEFT");

        // coordinate is null
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, null, new Coordinate(0, 0), false));

        // add a building on the road path
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1, 0));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(0, 0), false));

        // make the path not continuous
        coordinates.clear();
        coordinates.add(new Coordinate(1, 3));
        coordinates.add(new Coordinate(2, 5));
        assertThrows(InvocationTargetException.class, ()->placeRoadMethod.invoke(road, coordinates, new Coordinate(0, 0), false));
    }

    @Test
    void test_chooseBuildingPort() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        Road road = new Road();
        assertNull(road.chooseBuildingPort(coordinates));
        Coordinate c1 = new Coordinate(1,1);
        Coordinate c2 = new Coordinate(1,2);
        Coordinate c3 = new Coordinate(1,3);
        Coordinate c4 = new Coordinate(2,2);
        coordinates.add(c1);
        coordinates.add(c2);
        coordinates.add(c3);
        coordinates.add(c4);
        Road.board.setBoardPosStatus(c1, 1);
        Road.board.setBoardPosStatus(c2, 1);
        Road.board.setBoardPosStatus(c3, 1);
        Road.board.setBoardPosStatus(c4, 1);
        assertEquals(new Coordinate(1, 4), road.chooseBuildingPort(coordinates));
        AtomBuilding b1 = new AtomBuilding(new Coordinate(0, 3));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(1, 4));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(2, 3));
        assertNull(road.chooseBuildingPort(coordinates));
    }

    @Test
    void test_earlyStop() {

    }

    @Test
    void test_generateRoad_new() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        Road road = new Road(b1, b2);
//        确认路已修建，且全局可见
        assertEquals(1, getTotalRoads());
        ArrayList<RoadTile> roadTiles = road.roadTiles;
        assertEquals(19, roadTiles.size());
    }

    @Test
    void test_generateRoad_special() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
//        起点和终点相同，不应该生成新路
        Road road1 = new Road(b1, b1);
        assertEquals(0, getTotalRoads());
        Road road2 = new Road(b1, b2);
//        同起点同终点已有路，不应该生成新路
        Road road3 = new Road(b1, b2);
        assertEquals(1, getTotalRoads());
    }

    @Test
//    测building中转
    void test_generateRoad_reuse1() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        Road road1 = new Road(b3, b2);
//        确认路已修建，且全局可见
        assertEquals(1, getTotalRoads());
//        确认路线正确
        ArrayList<RoadTile> roadTiles = road1.roadTiles;
        assertEquals(10, roadTiles.size());
        Road road2 = new Road(b1, b2);
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        assertEquals(11, roadTiles2.size());
    }

    @Test
//    测垂线段绘制
    void test_generateRoad_reuse2() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(5,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 3));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 3));
        Road road1 = new Road(b2, b3);
//        确认路已修建，且全局可见
        assertEquals(1, getTotalRoads());
        Road road2 = new Road(b1, b3);
        assertEquals(2, getTotalRoads());
        Road road3 = new Road(b1, b2);
        assertEquals(3, getTotalRoads());

        //        确认路线正确
        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2= road2.roadTiles;
        ArrayList<RoadTile> roadTiles3 = road3.roadTiles;
        assertEquals(10, roadTiles1.size());
        assertEquals(3, roadTiles2.size());
        assertEquals(6, roadTiles3.size());
    }

    @Test
//    测放弃reuse
    void test_generateRoad_reuse3() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(20, 20));
        Road road1 = new Road(b3, b2);
//        确认路已修建，且全局可见
        assertEquals(1, getTotalRoads());
        Road road2 = new Road(b1, b2);
        assertEquals(2, getTotalRoads());

        //        确认路线正确
        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        assertEquals(21, roadTiles1.size());
        assertEquals(19, roadTiles2.size());
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
        assertEquals(3, Road.existingRoads.size());
//        确认路线正确
        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        ArrayList<RoadTile> roadTiles3 = road3.roadTiles;
        assertEquals(10, roadTiles1.size());
        assertEquals(19, roadTiles2.size());
        assertEquals(18, roadTiles3.size());
    }

    @Test
//    测n 对 1，这个例子适合展示
    void test_generateRoad_reuse5() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(10,10));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(1, 1));
        AtomBuilding b3 = new AtomBuilding(new Coordinate(1, 10));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(1, 20));
        Road road1 = new Road(b2, b1);
        assertEquals(1, getTotalRoads());
        Road road2 = new Road(b3, b1);
        assertEquals(2, getTotalRoads());
        Road road3 = new Road(b4, b1);
        assertEquals(3, getTotalRoads());

        ArrayList<RoadTile> roadTiles1 = road1.roadTiles;
        ArrayList<RoadTile> roadTiles2 = road2.roadTiles;
        ArrayList<RoadTile> roadTiles3 = road3.roadTiles;
        assertEquals(17, roadTiles1.size());
        assertEquals(7, roadTiles2.size());
        assertEquals(12, roadTiles3.size());
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
        assertEquals(0, getTotalRoads());
    }

    @Test
    void test_generateRoad_unreachable2() {
        AtomBuilding b1 = new AtomBuilding(new Coordinate(1,1));
        AtomBuilding b2 = new AtomBuilding(new Coordinate(10, 10));
        AtomBuilding b = new AtomBuilding(new Coordinate(15, 10));
        Road road1 = new Road(b, b2);
        assertEquals(1, getTotalRoads());

        AtomBuilding b3 = new AtomBuilding(new Coordinate(3, 0));
        AtomBuilding b4 = new AtomBuilding(new Coordinate(3, 1));
        AtomBuilding b5 = new AtomBuilding(new Coordinate(3, 2));
        AtomBuilding b6 = new AtomBuilding(new Coordinate(3, 3));
        AtomBuilding b7 = new AtomBuilding(new Coordinate(2, 3));
        AtomBuilding b8 = new AtomBuilding(new Coordinate(1, 3));
        AtomBuilding b9 = new AtomBuilding(new Coordinate(0, 3));
        Road road2 = new Road(b1, b2);
        assertEquals(1, getTotalRoads());
    }

    @Test
    void test_shortestPath() {
        Coordinate c1 = new Coordinate(0, 0);
        Coordinate c2 = new Coordinate(5, 2);
        ArrayList<Coordinate> p1 = new ArrayList<>();
        assertEquals(7, Road.shortestPath(c1, c2, p1));
//        System.out.println(p1);
        Road.board.setBoardPosStatus(new Coordinate(1, 0), 1);
        Road.board.setBoardPosStatus(new Coordinate(1, 1), 1);
        Road.board.setBoardPosStatus(new Coordinate(1, 2), 1);
        Road.board.setBoardPosStatus(new Coordinate(1, 3), 1);
        ArrayList<Coordinate> p2 = new ArrayList<>();
        assertEquals(11, Road.shortestPath(c1, c2, p2));
//        System.out.println(p2);
        Road.board.setBoardPosStatus(new Coordinate(0, 3), 1);
        ArrayList<Coordinate> p3 = new ArrayList<>();
        assertEquals(-1, Road.shortestPath(c1, c2, p3));
    }
}