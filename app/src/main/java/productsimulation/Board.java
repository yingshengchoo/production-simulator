package productsimulation;

import productsimulation.model.Building;

import java.util.HashMap;

public class Board {
    private final int height;
    private final int width;
    private static Board singleton = null;
    private static final int HEIGHT = 100;
    private static final int WIDTH = 100;

    private Board(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public static Board getBoard() {
        if(singleton == null) {
            singleton = new Board(HEIGHT, WIDTH);
        }
        return singleton;
    }

    // 仅用于测试
    public void cleanup() {
        singleton.boardPosWeight.clear();
    }

    // 之前设计全部推翻。现在把权值设在坐标上，用于确认最短路。
    // 界外，或者已经有普通建筑，则权值为正无穷，
    // 如果是空白状态，权值为2，
    // 如果已经有路，权值为1
    private HashMap<Coordinate, Integer> boardPosWeight = new HashMap<>();

    // only for load
    public void setWeight(HashMap<Coordinate, Integer> weight) {
        boardPosWeight = weight;
    }
    // only for save
    public HashMap<Coordinate, Integer> getWeight() {
        return boardPosWeight;
    }

    // placeholder, 盘子大小肯定还要再议、规范化为大写常量
    public boolean isOutOfBound(Coordinate c) {
        if(c.x < 0 || c.x > WIDTH) {
            return true;
        }
        if(c.y < 0 || c.y > HEIGHT) {
            return true;
        }
        return false;
    }

    public int getBoardPosWeight(Coordinate c) {
        if(isOutOfBound(c)) {
            return Integer.MAX_VALUE;
        }
        return boardPosWeight.getOrDefault(c, 2);
    }

//    不进行任何检查，直接覆盖写，caller自行负责安全调用
    public void setBoardPosWeight(Coordinate c, int weight) {
        boardPosWeight.put(c, weight);
    }

    public void addBuilding(Building b) {
        this.boardPosWeight.put(b.getCoordinate(), Integer.MAX_VALUE);
    }
}
