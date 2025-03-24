package productsimulation.request.sourcePolicy.Estimate;

import productsimulation.model.Building;

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
        int res = building.getStorage().get(ingredient);
        for (Entry entry : set) {
            if (entry.getBuilding().equals(building) && entry.getIngredient().equals(ingredient)) {
                res -= entry.getAmount();
            }
        }

        return Math.max(res, 0);
    }

    public boolean isRecorded(Building building, String item) {
        for (Entry entry : set) {
            if (entry.getBuilding().equals(building) && entry.getItem().equals(item)) {
                return true;
            }
        }
        return false;
    }

    public void removeByPath(Path pathToRemove) {
        set.removeIf(e -> pathToRemove.isPrefixOf(e.getPath()));
    }

    public void addRecord(Building building, Path path, String item) {
        Entry entry = new Entry(path, item, building, "", 0);
        set.add(entry);
    }
}
