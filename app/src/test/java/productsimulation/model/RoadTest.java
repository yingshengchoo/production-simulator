package productsimulation.model;

import org.junit.jupiter.api.Test;
import productsimulation.Coordinate;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoadTest {

    @Test
    void placeRoad() {
    }

    @Test
    void generateRoad() {
    }

    @Test
    void shortestPath() {
        Coordinate c1 = new Coordinate(0, 0);
        Coordinate c2 = new Coordinate(5, 2);
        ArrayList<Coordinate> p1 = new ArrayList<>();
        assertEquals(7, Road.shortestPath(c1, c2, p1));
//        System.out.println(p1);
        Road.board.placeOnBoard(new Coordinate(1, 0));
        Road.board.placeOnBoard(new Coordinate(1, 1));
        Road.board.placeOnBoard(new Coordinate(1, 2));
        Road.board.placeOnBoard(new Coordinate(1, 3));
        ArrayList<Coordinate> p2 = new ArrayList<>();
        assertEquals(11, Road.shortestPath(c1, c2, p2));
//        System.out.println(p2);
    }
}