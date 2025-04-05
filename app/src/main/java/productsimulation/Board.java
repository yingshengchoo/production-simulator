package productsimulation;

import java.util.HashMap;

// 目前考虑作为GlobalModelManager的成员
// 目前的唯一作用是：确保一个位置最多有一个建筑
public class Board {
    private HashMap<Coordinate, Boolean> boardPosStatus;

    public boolean isOccupied(Coordinate c) {
        return boardPosStatus.getOrDefault(c, false);
    }
    public boolean placeOnBoard(Coordinate c) {
        if(!isOccupied(c)) {
            boardPosStatus.put(c, true);
            return true;
        }
        return false;
    }
}
