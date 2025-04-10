package productsimulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productsimulation.model.road.Road;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @BeforeEach
    public void setUp() {
        Board.cleanup();
    }

    @Test
//    由于右边界和上边界没定，暂时只测左边界和下边界
    void isOutOfBound() {
        Coordinate c1 = new Coordinate(-1, 0);
        Coordinate c2 = new Coordinate(0, 0);
        Coordinate c3 = new Coordinate(0, -1);
        Board board = Board.getBoard();
        assertTrue(board.isOutOfBound(c1));
        assertFalse(board.isOutOfBound(c2));
        assertTrue(board.isOutOfBound(c3));
    }

    @Test
    void test_board() {
        Board board = Board.getBoard();
        Coordinate c1 = new Coordinate(2, 3);
        assertEquals(2, board.getBoardPosWeight(c1));
        board.setBoardPosWeight(c1, 1);
        assertEquals(1, board.getBoardPosWeight(c1));
        Coordinate c2 = new Coordinate(-1, 0);
        assertEquals(Integer.MAX_VALUE, board.getBoardPosWeight(c2));
    }
}