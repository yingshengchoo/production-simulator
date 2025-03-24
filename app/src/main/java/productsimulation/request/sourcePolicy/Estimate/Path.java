package productsimulation.request.sourcePolicy.Estimate;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private final List<Segment> segments;

    public Path() {
        segments = new ArrayList<>();
    }

    public Path(List<Segment> segments) {
        this.segments = segments;
    }

    public Path append(Segment seg) {
        List<Segment> newList = new ArrayList<>(this.segments);
        newList.add(seg);
        return new Path(newList);
    }

    public boolean isPrefixOf(Path other) {
        if (segments.size() > other.segments.size()) {
            return false;
        }

        for (int i = 0; i < segments.size(); i++) {
            if (!segments.get(i).equals(other.segments.get(i))) {
                return false;
            }
        }
        return true;
    }
}


