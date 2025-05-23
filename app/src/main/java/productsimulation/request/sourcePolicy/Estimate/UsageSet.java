package productsimulation.request.sourcePolicy.Estimate;

import productsimulation.model.Building;
import productsimulation.request.Request;

import java.util.HashSet;
import java.util.Set;

public class UsageSet {
    private Set<Entry> set;

    public UsageSet() {
        this.set = new HashSet<>();
    }

    public void add(Entry entry) {
        set.add(entry);
    }

    public int getIngredientRemain(Building building, String ingredient) {
        int res = building.getStorage().getOrDefault(ingredient, 0);
        for (Entry entry : set) {
            if (entry.getBuilding().equals(building) && entry.getIngredient().equals(ingredient)) {
                res -= entry.getAmount();
            }
        }

        return Math.max(res, 0);
    }

    public boolean isRecorded(Request request, Building building) {
        for (Entry entry : set) {
            if (request.isSameItemRequester(entry.getRequest()) && building.equals(entry.getBuilding())) {
                return true;
            }
        }
        return false;
    }

    public void removeByPath(Path pathToRemove) {
        set.removeIf(e -> pathToRemove.isPrefixOf(e.getPath()));
    }

    public void recordWorking(Building building, Path path, Request request) {
        Entry entry = new Entry(path, request, building, "", 0);
        set.add(entry);
    }
}

