package productsimulation.request.sourcePolicy.Estimate;

import productsimulation.model.Building;


import java.util.Objects;

public class Segment {

    private final int uniqueId;
    private final Building building;

    public Segment(int uniqueId, Building building) {
        this.uniqueId = uniqueId;
        this.building = building;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;
        return uniqueId == segment.uniqueId && Objects.equals(building, segment.building);
    }

    @Override
    public int hashCode() {
        int result = uniqueId;
        result = 31 * result + Objects.hashCode(building);
        return result;
    }

    public Building getBuilding() {
        return building;
    }
}
