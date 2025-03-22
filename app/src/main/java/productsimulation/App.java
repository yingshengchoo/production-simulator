package productsimulation;

import productsimulation.command.*;
import productsimulation.model.*;
import productsimulation.request.OneTimeServePolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SoleSourcePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.Request;

import java.util.*;


public class App {

    private static void minimalE2E() {
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

    public static void main(String[] args) {
        minimalE2E();
    }
}
