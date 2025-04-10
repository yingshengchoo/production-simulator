package productsimulation;

import java.util.HashMap;

// 目前考虑作为GlobalModelManager的成员
// 目前的唯一作用是：确保一个位置最多有一个建筑
public class Board {
    // 路不能修在建筑上，建筑不能修在路上，但路可以修在路上
    // 本该用Enum，但clover有问题，故用整数。-1代表界外，0代表没任何建筑，1代表有普通建筑，2代表有路
    private final HashMap<Coordinate, Integer> boardPosStatus = new HashMap<>();

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

    public int getBoardPosStatus(Coordinate c) {
        if(isOutOfBound(c)) {
            return -1;
        }
        return boardPosStatus.getOrDefault(c, 0);
    }

//    不进行任何检查，直接覆盖写，caller自行负责安全调用
    public void setBoardPosStatus(Coordinate c, int type) {
        boardPosStatus.put(c, type);
    }
}
