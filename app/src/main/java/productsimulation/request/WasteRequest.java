package productsimulation.request;

import productsimulation.model.Building;
import productsimulation.model.Recipe;

import java.io.Serializable;

/**
 * WasteRequest extends Request to carry a specified count of waste items.
 */
public class WasteRequest extends Request implements Serializable {
    private final int count;

    /**
     * Constructs a WasteRequest transporting 'count' of the given ingredient
     * from source building to requester building (waste disposal).
     * @param ingredient the waste item name
     * @param count      how many units of waste
     * @param source     the building producing the waste
     * @param requester  the disposal building
     * @param transLatency transport latency in timesteps
     */
    public WasteRequest(String ingredient,
                        int count,
                        Building source,
                        Building requester,
                        int transLatency) {
        super(ingredient, Recipe.getRecipe(ingredient), requester, transLatency);
        this.count = count;
        setWorker(source);
    }

    /**
     * Returns the number of waste items to transport.
     */
    public int getCount() {
        return count;
    }
}

