package productsimulation;

import java.util.HashMap;

// 目前考虑作为GlobalModelManager的成员
// 目前的唯一作用是：确保一个位置最多有一个建筑
public class Board {
    private final HashMap<Coordinate, Boolean> boardPosStatus = new HashMap<>();

    // placeholder, 盘子大小肯定还要再议、规范化为大写常量
    public boolean isOutOfBound(Coordinate c) {
        if(c.x < 0 || c.x > 100) {
            return true;
        }
        if(c.y < 0 || c.y > 100) {
            return true;
        }
        return false;
    }

    public boolean isOccupied(Coordinate c) {
        if(isOutOfBound(c)) {
            return true;
        }
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
