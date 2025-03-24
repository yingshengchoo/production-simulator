package productsimulation.request.sourcePolicy.Estimate;

public class IdGenerator {
    static int id = 0;

    public static int nextId() {
        return id++;
    }

    public static void reset() {
        id = 0;
    }
}
