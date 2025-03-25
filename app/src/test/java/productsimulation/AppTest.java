package productsimulation;

import org.junit.jupiter.api.Disabled;
import productsimulation.command.FinishCommand;
import productsimulation.command.RequestCommand;
import productsimulation.command.StepCommand;
import productsimulation.model.*;
import productsimulation.request.OneTimeServePolicy;
import productsimulation.request.servePolicy.*;
import productsimulation.request.sourcePolicy.SoleSourcePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.junit.jupiter.api.Test;
import productsimulation.request.sourcePolicy.SourceQLen;
import productsimulation.setup.SetupParser;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppTest {
    @Test
    public void minimalE2E() {
        // step 1: setup
        // skip parsing the recipe
        Recipe woodMineRecipe = new Recipe(1, new LinkedHashMap<String, Integer>(), "wood");
        LinkedHashMap<String, Integer> woodenSwordIngredients = new LinkedHashMap<>();
        woodenSwordIngredients.put("wood", 2);
        Recipe woodSwordRecipe = new Recipe(1, woodenSwordIngredients, "wooden_sword");
        // skip parsing the type
        Map<String, Recipe> woodMineRecipes = new HashMap<>();
        woodMineRecipes.put("wood", woodMineRecipe);
        FactoryType woodMineType = new FactoryType("wood_mine", woodMineRecipes);
        Map<String, Recipe> woodSwordRecipes = new HashMap<>();
        woodSwordRecipes.put("wooden_sword", woodSwordRecipe);
        FactoryType woodSwordType = new FactoryType("sword_factory", woodSwordRecipes);
        // skip serving policy and request policy
        SourcePolicy soleSourcePolicy = new SoleSourcePolicy();
        ServePolicy oneTimeServePolicy = new OneTimeServePolicy();
        // skip parsing the building
        Mine woodMine = new Mine("FirstWoodMine", woodMineType,
                new ArrayList<Building>(), soleSourcePolicy, oneTimeServePolicy);
        List<Building> sources = new ArrayList<>();
        sources.add(woodMine);
        Factory woodSwordFactory = new Factory("FirstWoodenSwordFactory", woodSwordType,
                sources, soleSourcePolicy, oneTimeServePolicy);

        // step 2: get user prompt and print results
        // skip parsing the command
        // skip save/load
        // skip finish command
        LogicTime logicTime = LogicTime.getInstance();
        logicTime.addObservers(woodMine);
        logicTime.addObservers(woodSwordFactory);
        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
        requestBroadcaster.addRecipes(woodMineRecipe);
        requestBroadcaster.addRecipes(woodSwordRecipe);
        requestBroadcaster.addBuildings(woodMine);
        requestBroadcaster.addBuildings(woodSwordFactory);

        RequestCommand requestCommand = new RequestCommand("wooden_sword", "FirstWoodenSwordFactory");
        requestCommand.execute();
        StepCommand stepCommand = new StepCommand(1);
        stepCommand.execute();
        stepCommand.execute();
        stepCommand.execute();
        stepCommand.execute();
    }

    private void demoHelper(String demoName) {
        SetupParser parser = new SetupParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(demoName);
        assertNotNull(inputStream);
        parser.parse(new BufferedReader(new InputStreamReader(inputStream)));
        Map<String, Recipe> recipes = parser.getRecipeMap();
        Map<String, Building> buildings = parser.getBuildingMap();

        LogicTime logicTime = LogicTime.getInstance();
        RequestBroadcaster requestBroadcaster = RequestBroadcaster.getInstance();
        SourcePolicy sourcePolicy = new SourceQLen();
        ServePolicy servePolicy = new FIFOPolicy();

        for(Building b: buildings.values()) {
            b.changeSourcePolicy(sourcePolicy);
            b.changeServePolicy(servePolicy);

            logicTime.addObservers(b);
            requestBroadcaster.addBuildings(b);
        }

        for(Recipe r: recipes.values()) {
            requestBroadcaster.addRecipes(r);
        }

        RequestCommand requestCommand = new RequestCommand("door", "D");
        requestCommand.execute();
        FinishCommand finishCommand = new FinishCommand();
        finishCommand.execute();
    }

    @Test
    public void door1Demo() {
        demoHelper("doors1.json");
    }

    @Test
    public void door2Demo() {
        demoHelper("doors2.json");
    }
}