package productsimulation.model.road;

import productsimulation.Coordinate;

public class RoadTile {
    Coordinate c;
    //        为了clover不用Enum
    Direction direction;
    //        与building紧邻的tile比较特殊，可以允许各种方向
    private boolean isEnd;

    RoadTile(Coordinate c, Direction direction) {
        this.c = c;
        this.direction = direction;
    }

    public Coordinate getCoordinate() {
        return c;
    }

    public Direction getDirection() { return direction; }

    public boolean getIsEnd() { return isEnd; }

    public void setIsEnd(boolean b) { isEnd = b; }
}