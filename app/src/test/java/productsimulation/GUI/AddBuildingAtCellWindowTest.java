package productsimulation.GUI;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import productsimulation.Coordinate;
import productsimulation.State;
import productsimulation.model.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddBuildingAtCellWindowTest {

    private static final Coordinate COORD = new Coordinate(5, 10);
    private static final String NAME = "MyBuilding";

    /**
     * Uses reflection to invoke the private createBuilding method.
     */
    private Building invokeCreateBuilding(State state,
                                          BuildingType type,
                                          List<Building> sources) throws Exception {
        Method m = AddBuildingAtCellWindow.class
                .getDeclaredMethod("createBuilding", State.class, Coordinate.class,
                        String.class, BuildingType.class, java.util.List.class);
        m.setAccessible(true);
        return (Building) m.invoke(null, state, AddBuildingAtCellWindowTest.COORD, AddBuildingAtCellWindowTest.NAME, type, sources);
    }

    @Test
    void testCreateBuilding_storage() throws Exception {
        State dummyState = Mockito.mock(State.class);
        StorageType storageType = new StorageType("storeType", 1.5, 100, "item");
        Building fakeStorage = Mockito.mock(Storage.class);

        try (MockedStatic<Storage> storageMock = Mockito.mockStatic(Storage.class)) {
            storageMock
                    .when(() -> Storage.addStorage(
                            Mockito.eq(NAME),
                            Mockito.anyList(),
                            Mockito.any(),
                            Mockito.any(),
                            Mockito.eq(COORD),
                            Mockito.eq(storageType)))
                    .thenReturn(fakeStorage);

            Building result = invokeCreateBuilding(
                    dummyState, storageType, Collections.emptyList());

            assertSame(fakeStorage, result,
                    "createBuilding should have returned the Storage returned by Storage.addStorage()");
        }
    }

    @Test
    void testCreateBuilding_mine() throws Exception {
        State dummyState = Mockito.mock(State.class);
        // Correct type of argument for the constructor
        Map<String, Recipe> emptyRecipeMap = Collections.emptyMap();
        BuildingType mineType = new BuildingType("Coal Mine", emptyRecipeMap);
        Mine fakeMine = Mockito.mock(Mine.class);

        try (MockedStatic<Mine> mineMock = Mockito.mockStatic(Mine.class)) {
            mineMock
                    .when(() -> Mine.addMine(
                            Mockito.eq(NAME),
                            Mockito.anyList(),
                            Mockito.any(),
                            Mockito.any(),
                            Mockito.eq(COORD),
                            Mockito.eq(mineType)))
                    .thenReturn(fakeMine);

            Building result = invokeCreateBuilding(
                    dummyState, mineType, Collections.emptyList());

            assertSame(fakeMine, result,
                    "createBuilding should have returned the Mine returned by Mine.addMine()");
        }
    }

    @Test
    void testCreateBuilding_factory() throws Exception {
        State dummyState = Mockito.mock(State.class);
        // Correct type of argument for the constructor
        Map<String, Recipe> emptyRecipeMap = Collections.emptyMap();
        BuildingType factoryType = new BuildingType("Widget Factory", emptyRecipeMap);
        Factory fakeFactory = Mockito.mock(Factory.class);

        try (MockedStatic<Factory> factoryMock = Mockito.mockStatic(Factory.class)) {
            factoryMock
                    .when(() -> Factory.addFactory(
                            Mockito.eq(NAME),
                            Mockito.anyList(),
                            Mockito.any(),
                            Mockito.any(),
                            Mockito.eq(COORD),
                            Mockito.eq(factoryType)))
                    .thenReturn(fakeFactory);

            Building result = invokeCreateBuilding(
                    dummyState, factoryType, Collections.emptyList());

            assertSame(fakeFactory, result,
                    "createBuilding should have returned the Factory returned by Factory.addFactory()");
        }
    }
}
