package productsimulation.model;

import productsimulation.Board;
import productsimulation.Coordinate;

import java.util.HashSet;

// road容积无限，但不模拟运输过程。由request完成时的hook函数在latency回合后传送到目标仓库。
// GUI上点击road，只显示方向信息。road上也不准备做“有个货物在动”的动画。
public class Road {
    // 此处Road作为第一个caller，先new用着，之后改成getBoard
    Board board = new Board();

    // 需要一个全局Road，暂时先放road类里，但是要有，以便全盘考虑路线规划等问题
    static HashSet<RoadTile> roadTiles = new HashSet<>();

    //      道路交汇处可以有两个方向，用掩码来表示
    public class Direction {
        public static final int UP = 1 << 0;
        public static final int DOWN = 1 << 1;
        public static final int LEFT = 1 << 2;
        public static final int RIGHT = 1 << 3;

        private int mask;

        public Direction(int mask) {
            this.mask = mask;
        }

        public boolean hasDirection(int direction) {
            return (mask & direction) != 0;
        }

        public void addDirection(int direction) {
            mask |= direction;
        }

        public void removeDirection(int direction) {
            mask &= ~direction;
        }

        public void clear() {
            mask = 0;
        }

        public String getDirections() {
            StringBuilder sb = new StringBuilder();
            if (hasDirection(UP)) sb.append("UP ");
            if (hasDirection(DOWN)) sb.append("DOWN ");
            if (hasDirection(LEFT)) sb.append("LEFT ");
            if (hasDirection(RIGHT)) sb.append("RIGHT ");
            return sb.toString().trim();
        }

        // 获取当前掩码
        public int getMask() {
            return mask;
        }

        // 静态方法创建方向
        public Direction UP() {
            return new Direction(UP);
        }

        public Direction DOWN() {
            return new Direction(DOWN);
        }

        public Direction LEFT() {
            return new Direction(LEFT);
        }

        public Direction RIGHT() {
            return new Direction(RIGHT);
        }
    }

    class RoadTile {
        Coordinate c;
        //        为了clover不用Enum
        Direction direction;
        //        与building紧邻的tile比较特殊，可以允许各种方向
        boolean isEnd;

        RoadTile(Coordinate c, Direction direction) {
            this.c = c;
            this.direction = direction;
        }

        public Coordinate getCoordinate() {
            return c;
        }
    }

    public void placeRoad(Iterable<RoadTile> tiles) {
        for(RoadTile tile: tiles) {
            Coordinate c = tile.getCoordinate();
            if(board.placeOnBoard(c)) {
                roadTiles.add(tile);
            }
        }
    }

    public void generateDirectRoad(Coordinate start, Coordinate end) {
        // 如果start end重合或相邻，无需生成新tile

        // 两点的中间可能有阻隔


    }
}
