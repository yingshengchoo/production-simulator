package productsimulation.request;

import java.io.Serializable;

/**
 * A utility class for generating unique sequential IDs.
 * The class maintains an internal counter that increments with each call to the nextId method,
 * ensuring that each ID is unique and sequential.
 * //todo: Thread-safety must be ensured externally if used in a multi-threaded context.
 */
public class IdGenerator implements Serializable{


    private int id = 0;
    public int nextId() {
        return id++;
    }
}
