package productsimulation;

import org.junit.jupiter.api.Test;
import productsimulation.model.Building;
import productsimulation.model.BuildingType;
import productsimulation.model.Mine;
import productsimulation.model.Recipe;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

class LogicTimeTest {
    @BeforeEach
    void setUp() {
        LogicTime.getInstance().reset();
    }

    @Test
    void test_singleton() {
        // multiple getInstance should be the same object
        LogicTime t1 = LogicTime.getInstance();
        t1.stepNHandler(1);
        LogicTime t2 = LogicTime.getInstance();
        assertEquals(t1, t2);
    }

    @Test
    void test_editObservers() {
        LogicTime t = LogicTime.getInstance();

        Recipe woodMineRecipe = new Recipe(1, new HashMap<String, Integer>(),"wood");
        Map<String, Recipe> woodMineRecipes = new HashMap<>();
        woodMineRecipes.put("wood", woodMineRecipe);
        BuildingType woodMineType = new BuildingType("wood_mine", woodMineRecipes);
        SourcePolicy soleSourcePolicy = new SoleSourcePolicy();
        ServePolicy oneTimeServePolicy = new OneTimeServePolicy();
        Mine woodMine = new Mine("FirstWoodMine", woodMineType,
                new ArrayList<Building>(), soleSourcePolicy, oneTimeServePolicy);

        t.addObservers(woodMine);
        assertEquals(t.getObserversSize(), 1);

        Mine woodMine2 = new Mine("SecondWoodMine", woodMineType,
                new ArrayList<Building>(), soleSourcePolicy, oneTimeServePolicy);

        t.addObservers(woodMine2);
        assertEquals(t.getObserversSize(), 2);

        t.removeObservers(woodMine);
        assertEquals(t.getObserversSize(), 1);

        Mine woodMine3 = new Mine("ThirdWoodMine", woodMineType,
                new ArrayList<Building>(), soleSourcePolicy, oneTimeServePolicy);

        t.removeObservers(woodMine3);
        assertEquals(t.getObserversSize(), 1);

        t.stepNHandler(5);
        assertEquals(t.getStep(), 5);
    }

    @Test
    void test_finishHandler() {
        LogicTime t = LogicTime.getInstance();
        t.finishHandler();
        assertEquals(t.getStep(), 0);
    }
}