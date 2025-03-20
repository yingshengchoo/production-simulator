package productsimulation;

import productsimulation.command.*;
import productsimulation.model.*;
import productsimulation.request.OneTimeServePolicy;
import productsimulation.request.ServePolicy;
import productsimulation.sourcePolicy.SoleSourcePolicy;
import productsimulation.sourcePolicy.SourcePolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class App {

  private static void minimalE2E() {
    // step 1: setup
    // skip parsing the recipe
    Recipe woodMineRecipe = new Recipe(1, new HashMap<String, Integer>(),"wood");
    HashMap<String, Integer> woodenSwordIngredients = new HashMap<>();
    woodenSwordIngredients.put("wood", 2);
    Recipe woodSwordRecipe = new Recipe(1, woodenSwordIngredients, "wooden_sword");
    // skip parsing the type
    List<Recipe> woodMineRecipes = new ArrayList<>();
    woodMineRecipes.add(woodMineRecipe);
    FactoryType woodMineType = new FactoryType("wood_mine", woodMineRecipes);
    List<Recipe> woodSwordRecipes = new ArrayList<>();
    woodSwordRecipes.add(woodSwordRecipe);
    FactoryType woodSwordType = new FactoryType("sword_factory", woodSwordRecipes);
    // skip serving policy and request policy
    SourcePolicy soleSourcePolicy = new SoleSourcePolicy();
    ServePolicy oneTimeServePolicy = new OneTimeServePolicy();
    // skip parsing the building
    Mine woodMine = new Mine("OneWoodMine", woodMineType,
            new ArrayList<Building>(), soleSourcePolicy, oneTimeServePolicy);
    List<Building> sources = new ArrayList<>();
    sources.add(woodMine);
    Factory woodSwordFactory = new Factory("OneWoodenSwordFactory", woodSwordType,
            sources, soleSourcePolicy, oneTimeServePolicy);

    // step 2: get user prompt and print results
    // skip parsing the command
    // skip save/load
    RequestCommand requestCommand = new RequestCommand();
    requestCommand.execute();
    StepCommand stepCommand = new StepCommand();
    stepCommand.execute();
    FinishCommand finishCommand = new FinishCommand();
    finishCommand.execute();
  }

  public static void main(String[] args) {
    minimalE2E();
  }
}
