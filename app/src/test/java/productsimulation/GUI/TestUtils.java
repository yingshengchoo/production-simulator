package productsimulation.GUI;

import productsimulation.Coordinate;
import productsimulation.LogicTime;
import productsimulation.RequestBroadcaster;
import productsimulation.State;
import productsimulation.model.*;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.sourcePolicy.SourceQLen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestUtils {
    public static State createDummyState() {
        Recipe door = new Recipe(10, Map.of("wood", 1, "handle", 1, "hinge", 3), "door");
        Recipe handle = new Recipe(5, Map.of("metal", 1), "handle");
        Recipe hinge = new Recipe(1, Map.of("metal", 1), "hinge");
        Recipe wood = new Recipe(1, Collections.emptyMap(), "wood");
        Recipe metal = new Recipe(1, Collections.emptyMap(), "metal");

        List<Recipe> recipeList = new ArrayList<>();
        recipeList.add(door);
        recipeList.add(handle);
        recipeList.add(hinge);
        recipeList.add(wood);
        recipeList.add(metal);

        BuildingType doorFactory = new BuildingType("Door Factory", Map.of("door", door));
        BuildingType componentFactory = new BuildingType("Component Factory", Map.of("handle", handle, "hinge", hinge));
        BuildingType woodMineType = new BuildingType("wood", Map.of("wood", wood));
        BuildingType metalMineType = new BuildingType("metal", Map.of("metal", metal));

        List<BuildingType> typeList = new ArrayList<>();
        typeList.add(doorFactory);
        typeList.add(componentFactory);
        typeList.add(woodMineType);
        typeList.add(metalMineType);

        Factory factoryA = new Factory("Factory A", doorFactory, new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), new Coordinate(10, 10));
        Factory compFactoryA = new Factory("Component Factory A", componentFactory, new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), new Coordinate(8, 9));
        Mine woodMine = new Mine("Wood Mine", woodMineType, new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), new Coordinate(5, 10));
        Mine metalMine = new Mine("Metal Mine", metalMineType, new ArrayList<>(), new SourceQLen(), new FIFOPolicy(), new Coordinate(4, 11));

        factoryA.setSources(List.of(woodMine, compFactoryA));
        compFactoryA.setSources(List.of(metalMine));
        woodMine.setSources(Collections.emptyList());
        metalMine.setSources(Collections.emptyList());

        List<Building> buildingList = new ArrayList<>();
        buildingList.add(factoryA);
        buildingList.add(compFactoryA);
        buildingList.add(woodMine);
        buildingList.add(metalMine);

        RequestBroadcaster rb = RequestBroadcaster.getInstance();
        LogicTime lt = LogicTime.getInstance();

        State.initialize(buildingList, typeList, recipeList, rb, lt);
        return State.getInstance();
    }
}
