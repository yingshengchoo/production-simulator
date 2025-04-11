package productsimulation.model;

import java.util.HashMap;
import java.util.Map;

public class StorageType extends BuildingType{
    double priority;
    int capacity;
    String itemToStore;

    public StorageType(String name, double priority, int capacity, String itemToStore) {
        super(name, new HashMap<String, Recipe>());
        this.priority = priority;
        this.capacity = capacity;
        this.itemToStore = itemToStore;
    }

    public int getCapacity() {
        return capacity;
    }



    public String getItemToStore() {
        return itemToStore;
    }



    public double getPriority() {
        return priority;
    }

}
