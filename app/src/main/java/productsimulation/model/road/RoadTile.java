package productsimulation.model.road;

import productsimulation.Coordinate;
import productsimulation.model.Cost;
import java.io.Serializable;


public class RoadTile implements Serializable {
    private final Coordinate c;
    private final Direction from;
    private final Direction to;
    private final Cost cost;
  
    public RoadTile(Coordinate c, Cost cost) {
        this.c = c;
        from = new Direction(Direction.UNDEFINED);
        to = new Direction(Direction.UNDEFINED);
        this.cost = cost;
    }

  public RoadTile(Coordinate c){
    this(c, new Cost());
  }

    public void setDirection(Coordinate lastPos, Coordinate curPos, Coordinate nextPos) {
        // 确定 from 方向（从 lastPos 到 curPos）
        if (lastPos != null) {
            int dx = curPos.x - lastPos.x;
            int dy = curPos.y - lastPos.y;

            if (dx > 0) from.addDirection(Direction.LEFT);       // 如果当前位置在上一个位置右边，则方向是从左来
            else if (dx < 0) from.addDirection(Direction.RIGHT); // 如果当前位置在上一个位置左边，则方向是从右来

            if (dy > 0) from.addDirection(Direction.UP);         // 如果当前位置在上一个位置下边，则方向是从上来
            else if (dy < 0) from.addDirection(Direction.DOWN);  // 如果当前位置在上一个位置上边，则方向是从下来
        }

        // 确定 to 方向（从 curPos 到 nextPos）
        if (nextPos != null) {
            int dx = nextPos.x - curPos.x;
            int dy = nextPos.y - curPos.y;

            if (dx > 0) to.addDirection(Direction.RIGHT);      // 如果下一个位置在当前位置右边，则方向是向右去
            else if (dx < 0) to.addDirection(Direction.LEFT);  // 如果下一个位置在当前位置左边，则方向是向左去

            if (dy > 0) to.addDirection(Direction.DOWN);       // 如果下一个位置在当前位置下边，则方向是向下去
            else if (dy < 0) to.addDirection(Direction.UP);    // 如果下一个位置在当前位置上边，则方向是向上去
        }
    }

    public Coordinate getCoordinate() {
        return c;
    }

    public Direction getFromDirection() { return from; }

    public Direction getToDirection() { return to; }
}
