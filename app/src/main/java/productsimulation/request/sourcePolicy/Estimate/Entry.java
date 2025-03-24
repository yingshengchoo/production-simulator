package productsimulation.request.sourcePolicy.Estimate;

import productsimulation.model.Building;

public class Entry {
    private final Path path;
    private final String item;
    private final Building building;
    private final String ingredient;
    private final int amount;

    public Entry(Path path, String item, Building building, String ingredient, int amount) {
        this.path = path;
        this.item = item;
        this.building = building;
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public String getItem() {
        return item;
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
