package productsimulation.request.sourcePolicy;

import org.junit.jupiter.api.Test;
import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.MockingEnv;
import productsimulation.request.Request;

import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static productsimulation.request.sourcePolicy.SourceEstimate.getTopK;

class SourceEstimateTest {
    MockingEnv mockingEnv = new MockingEnv();
    @Test
    void getSourceTest() {
        Building f3 = mockingEnv.getBuildings().get(2);
        Building f32 = mockingEnv.getBuildings().get(3);

        Request request = new Request("r3", Recipe.getRecipe("r3"), null);
        Request request2 = new Request("r3", Recipe.getRecipe("r3"), null);

        f3.addRequest(request);
        f3.addRequest(request2);

        SourceEstimate se = new SourceEstimate();

        assertEquals(f32, se.getSource(mockingEnv.getBuildings(), "r3"));
    }

    @Test
    void getSourceTest2() {
        SourceEstimate se = new SourceEstimate();

        Building f3 = mockingEnv.getBuildings().get(2);
        Building f32 = mockingEnv.getBuildings().get(3);

        Request request = new Request("r3", Recipe.getRecipe("r3"), null);
        Request request2 = new Request("r3", Recipe.getRecipe("r3"), null);

        f3.addRequest(request);
        f3.addRequest(request2);

        for (int i = 0; i < 21; i++) {
            for (Building b : mockingEnv.getBuildings()) {
                b.goOneStep();
            }
            for (Building b : mockingEnv.getBuildings()) {
                b.updateNotified();
            }
            se.getSource(mockingEnv.getBuildings(), "r3");
        }

        Request request3 = new Request("r3", Recipe.getRecipe("r3"), null);

        assertEquals(f32, se.getSource(mockingEnv.getBuildings(), "r3"));
    }

    @Test
    public void testMergeFunction_withDuplicateKeys() {

        Building building = mock(Building.class);

        Map.Entry<Building, Integer> entry1 = new AbstractMap.SimpleEntry<>(building, 10);
        Map.Entry<Building, Integer> entry2 = new AbstractMap.SimpleEntry<>(building, 20);

        Set<Map.Entry<Building, Integer>> entries = new LinkedHashSet<>();
        entries.add(entry1);
        entries.add(entry2);

        Map<Building, Integer> mockedMap = mock(Map.class);
        when(mockedMap.entrySet()).thenReturn(entries);

        Map<Building, Integer> result = getTopK(1, mockedMap);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(building));
        assertEquals(Integer.valueOf(10), result.get(building));
    }
}