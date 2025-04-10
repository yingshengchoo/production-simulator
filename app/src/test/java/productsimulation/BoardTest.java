package productsimulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
//    由于右边界和上边界没定，暂时只测左边界和下边界
    void isOutOfBound() {
        Coordinate c1 = new Coordinate(-1, 0);
        Coordinate c2 = new Coordinate(0, 0);
        Coordinate c3 = new Coordinate(0, -1);
        Board board = new Board();
        assertTrue(board.isOutOfBound(c1));
        assertFalse(board.isOutOfBound(c2));
        assertTrue(board.isOutOfBound(c3));
    }

    @Test
    void test_board() {
        Board board = new Board();
        Coordinate c1 = new Coordinate(2, 3);
        assertEquals(0, board.getBoardPosStatus(c1));
        board.setBoardPosStatus(c1, 1);
        assertEquals(1, board.getBoardPosStatus(c1));
        Coordinate c2 = new Coordinate(-1, 0);
        assertEquals(-1, board.getBoardPosStatus(c2));
    }
}