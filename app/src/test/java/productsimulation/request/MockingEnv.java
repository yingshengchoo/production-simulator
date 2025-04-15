package productsimulation.request;

import productsimulation.model.*;
import productsimulation.model.road.Road;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.sourcePolicy.SourceQLen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockingEnv {

    List<Building> buildings;

    public MockingEnv() {
        FIFOPolicy fifo = new FIFOPolicy();
        SourceQLen qlen = new SourceQLen();

        // init RecipeList
        Recipe r1 = new Recipe(10, new HashMap<>(), "r1").register();
        Recipe r2 = new Recipe(10, new HashMap<>(), "r2").register();
        Map<String, Integer> i1 = new HashMap<>();
        i1.put("r1", 1);
        i1.put("r2", 2);
        Map<String, Integer> i2 = new HashMap<>();
        i2.put("r1", 2);
        i2.put("r2", 1);
        Recipe r3 = new Recipe(10, i1, "r3").register();
        Recipe r4 = new Recipe(10, i2, "r4").register();

        // init Buildings
        BuildingType ft1 = new BuildingType("t1", Map.of("r1", r1));
        BuildingType ft2 = new BuildingType("t2", Map.of("r2", r2));
        BuildingType ft3 = new BuildingType("t3", Map.of("r3", r3));
        BuildingType ft4 = new BuildingType("t4", Map.of("r4", r4));
        Building b1 = new Mine("b1", ft1, null, qlen, fifo).register();
        Building b2 = new Mine("b2", ft2, null, qlen, fifo).register();
        Building b22 = new Mine("b22", ft2, null, qlen, fifo).register();
        Building b23 = new Mine("b23", ft2, null, qlen, fifo).register();
        Building f3 = new Factory("f3", ft3, List.of(b1, b2, b22, b23), qlen, fifo).register();
        Building f32 = new Factory("f32", ft3, List.of(b1, b2, b22, b23), qlen, fifo).register();
        Building f4 = new Factory("f4", ft4, List.of(b1, b2, b22, b23), qlen, fifo).register();
        buildings = List.of(b1, b2, f3, f32, f4, b22, b23);

        for(Building bsrc: buildings) {
            for(Building bdst: buildings) {
                Road tmp = Road.generateRoad(bsrc, bdst);
            }
        }
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public Building getBuilding() {
        return buildings.get(0);
    }

}
