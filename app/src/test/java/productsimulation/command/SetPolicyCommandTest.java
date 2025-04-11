package productsimulation.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productsimulation.Board;
import productsimulation.LogicTime;
import productsimulation.RequestBroadcaster;
import productsimulation.State;
import productsimulation.model.*;
import productsimulation.request.servePolicy.ReadyPolicy;
import productsimulation.request.servePolicy.SjfPolicy;
import productsimulation.request.sourcePolicy.SourceSimplelat;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SetPolicyCommandTest {
    @BeforeEach
    public void cleanUpBefore() {
        Board.getBoard().cleanup();
        Building.buildings.clear();
    }

    void setUpEnvironment() {
        ArrayList<Building> buildings = new ArrayList<>();
        Building mine = new Mine("G", new BuildingType("Gold", Collections.emptyMap()), new ArrayList<>(), null, null);
        buildings.add(mine);
        ArrayList<Building> sources = new ArrayList<>();
        sources.add(mine);
        Building factory = new Factory("GC", new BuildingType("GoldChain", Collections.emptyMap()), sources, null, null);
        buildings.add(factory);

        Map<String, Recipe> recipes = new HashMap<>();
        Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put("Egg", 2);
        Recipe eggroll = new Recipe(3, ingredients, "EggRoll");
        recipes.put("EggRoll", eggroll);

        ArrayList<Recipe> stateRecipes = new ArrayList<>();
        stateRecipes.add(eggroll);

        ArrayList<BuildingType> types = new ArrayList<>();
        types.add(new BuildingType("EggRoll", recipes));

        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
        requestBroadcaster.addRecipes(eggroll);
        requestBroadcaster.addBuildings(mine);
        requestBroadcaster.addBuildings(factory);

        LogicTime logicTime = LogicTime.getInstance();
        logicTime.addObservers(mine);
        logicTime.addObservers(factory);

        State.initialize(buildings, types, stateRecipes, requestBroadcaster, logicTime);
    }
    @Test
    void test_execute_specific_building() {
        setUpEnvironment();

        SetPolicyCommand cmd = new SetPolicyCommand("request", "GC","sjf");
        for (Building b : cmd.getTargetBuildings()) {
            assertEquals("GC", b.getName());
        }
    }

    @Test
    void test_execute_default_building() {
        setUpEnvironment();

        SetPolicyCommand cmd = new SetPolicyCommand("request", "default", "sjf");
        assertDoesNotThrow(() -> cmd.execute());
        for (Building building : State.getInstance().getBuildings()) {
           assertEquals(new SjfPolicy().getName(), building.getServePolicy().getName());
        }
    }

    @Test
    void test_execute_default_building_after_change_serve() {
        setUpEnvironment();
        SetPolicyCommand cm1 = new SetPolicyCommand("request", "GC", "ready");
        cm1.execute();
        for (Building building : cm1.getTargetBuildings()) {
            assertEquals(new ReadyPolicy().getName(), building.getServePolicy().getName());
        }

        SetPolicyCommand cmd = new SetPolicyCommand("request", "default", "sjf");
        assertDoesNotThrow(() -> cmd.execute());
        for (Building building : State.getInstance().getBuildings()) {
            if (!building.getName().equals("GC")) {
                assertEquals(new SjfPolicy().getName(), building.getServePolicy().getName());
            } else {
                assertEquals(new ReadyPolicy().getName(), building.getServePolicy().getName());
            }
        }
    }

    @Test
    void test_execute_all_building_after_change_serve() {
        setUpEnvironment();
        SetPolicyCommand cm1 = new SetPolicyCommand("request", "GC", "ready");
        cm1.execute();
        for (Building building : cm1.getTargetBuildings()) {
            assertEquals(new ReadyPolicy().getName(), building.getServePolicy().getName());
        }

        SetPolicyCommand cmd = new SetPolicyCommand("request", "*", "sjf");
        assertDoesNotThrow(() -> cmd.execute());
        for (Building building : State.getInstance().getBuildings()) {
                assertEquals(new SjfPolicy().getName(), building.getServePolicy().getName());

        }
    }

    @Test
    void test_execute_default_building_after_change_source() {
        setUpEnvironment();
        SetPolicyCommand cmd1 = new SetPolicyCommand("source", "default", "simplelat");
        cmd1.execute();
        SetPolicyCommand cmd2 = new SetPolicyCommand("source", "default", "qlen");
        cmd2.execute();
        assertEquals("simplelat", cmd1.getPolicy().getName());
    }

    @Test
    void test_execute_all_building_after_change_source() {
        setUpEnvironment();
        SetPolicyCommand cmd1 = new SetPolicyCommand("source", "GC", "simplelat");
        cmd1.execute();
        assertEquals(new SourceSimplelat().getName(), cmd1.getTargetBuildings().get(0).getSourcePolicy().getName());
        SetPolicyCommand cmd2 = new SetPolicyCommand("source", "*", "simplelat");
        cmd2.execute();
        for (Building building : State.getInstance().getBuildings()) {
            assertEquals(new SourceSimplelat().getName(), building.getSourcePolicy().getName());
        }

    }


}