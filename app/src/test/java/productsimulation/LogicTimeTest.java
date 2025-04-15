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
        Building.buildingGlobalList.clear();
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
    void test_finishHandler() {
        LogicTime t = LogicTime.getInstance();
        t.finishHandler();
        assertEquals(t.getStep(), 0);
    }
}