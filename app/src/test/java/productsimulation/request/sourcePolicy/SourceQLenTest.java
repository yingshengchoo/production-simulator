package productsimulation.request.sourcePolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productsimulation.Board;
import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.MockingEnv;
import productsimulation.request.Request;

import static org.junit.jupiter.api.Assertions.*;

class SourceQLenTest {
    @BeforeEach
    public void cleanUpBefore() {
        Board.getBoard().cleanup();
        Building.buildings.clear();
    }
    MockingEnv mockingEnv = new MockingEnv();

    @Test
    void getSourceTest() {
        Building f3 = mockingEnv.getBuildings().get(2);
        Building f32 = mockingEnv.getBuildings().get(3);

        Request request = new Request("r3", Recipe.getRecipe("r3"), null);
        Request request2 = new Request("r3", Recipe.getRecipe("r3"), null);
        // Request request3 = new Request("r3", Recipe.getRecipe("r3"), null);

        f3.addRequest(request);
        f3.addRequest(request2);

        SourceQLen qLen = new SourceQLen();

        assertEquals(f32, qLen.getSource(mockingEnv.getBuildings(), "r3"));
    }
}