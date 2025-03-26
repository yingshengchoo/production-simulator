package productsimulation.request.sourcePolicy.Estimate;

import productsimulation.model.Building;
import productsimulation.request.Request;

public class Entry {
    private final Path path;
    private final Request request;
    private final Building building;
    private final String ingredient;
    private final int amount;

    public Entry(Path path, Request request, Building building, String ingredient, int amount) {
        this.path = path;
        this.request = request;
        this.building = building;
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public Request getRequest() {
        return request;
    }

    public Building getBuilding() {
        return building;
    }

    public Path getPath() {
        return path;
    }

    public String getIngredient() {
        return ingredient;
    }

    public int getAmount() {
        return amount;
    }
}
