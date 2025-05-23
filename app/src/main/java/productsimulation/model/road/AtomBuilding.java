package productsimulation.model.road;

import productsimulation.Board;
import productsimulation.Coordinate;
import productsimulation.model.Building;
import productsimulation.model.BuildingType;
import productsimulation.model.Factory;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.sourcePolicy.SourceQLen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

// 有位置的、面积为1的building，仅供测试Road相关
public class AtomBuilding extends Building {
    Coordinate position;

    public AtomBuilding(Coordinate c) {
        super("noname", new BuildingType("typename", new HashMap<>()), new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), c);
        this.position = c;
        Board.getBoard().setBoardPosWeight(c, Integer.MAX_VALUE);
    }

    public AtomBuilding(Coordinate c, String name) {
        super(name, new BuildingType("typename", new HashMap<>()), new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), c);
        this.position = c;
        Board.getBoard().setBoardPosWeight(c, Integer.MAX_VALUE);
    }

    public AtomBuilding register() {
        buildingGlobalList.add(this);
        Board.getBoard().addBuilding(this);
        return this;
    }

    @Override
    public boolean goOneStep() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtomBuilding that = (AtomBuilding) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
