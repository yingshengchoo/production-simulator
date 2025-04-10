package productsimulation;

import java.util.Objects;
import java.io.Serializable;


public class Coordinate implements Serializable {
    public int x;
    public int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static int ManhattanDis(Coordinate c1, Coordinate c2) {
        return Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y);
    }

    public static boolean isNeighbor(Coordinate c1, Coordinate c2) {
        return ManhattanDis(c1, c2) == 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate that = (Coordinate) obj;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
