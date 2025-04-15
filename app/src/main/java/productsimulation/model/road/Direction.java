package productsimulation.model.road;

import java.io.Serializable;

//      道路交汇处可以有两个方向，用掩码来表示
public class Direction implements Serializable {
    public static final int UNDEFINED = 0;
    public static final int UP = 1 << 0;
    public static final int RIGHT = 1 << 1;
    public static final int DOWN = 1 << 2;
    public static final int LEFT = 1 << 3;

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

    public String getDirections() {
        StringBuilder sb = new StringBuilder();
        if (hasDirection(UP)) sb.append("UP ");
        if (hasDirection(DOWN)) sb.append("DOWN ");
        if (hasDirection(LEFT)) sb.append("LEFT ");
        if (hasDirection(RIGHT)) sb.append("RIGHT ");
        return sb.toString().trim();
    }
}
