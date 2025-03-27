package productsimulation.request.sourcePolicy;

import org.junit.jupiter.api.Test;
import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.MockingEnv;
import productsimulation.request.Request;

import static org.junit.jupiter.api.Assertions.*;

class SourceSimplelatTest {
    MockingEnv mockingEnv = new MockingEnv();
    @Test
    void getSourceTest() {
        Building f3 = mockingEnv.getBuildings().get(2);
        Building f32 = mockingEnv.getBuildings().get(3);

        Request request = new Request("r3", Recipe.getRecipe("r3"), null);
        Request request2 = new Request("r3", Recipe.getRecipe("r3"), null);

        f3.addRequest(request);
        f3.addRequest(request2);

        SourceSimplelat sim = new SourceSimplelat();

        assertEquals(f32, sim.getSource(mockingEnv.getBuildings(), "r3"));
    }
}