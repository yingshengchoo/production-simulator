package productsimulation.model;

import java.util.*;
import java.io.Serializable;


public class BuildingType implements Serializable {
  private final String name;
  private final Map<String, Recipe> recipes;
  private final Cost cost;

  /**
   * Constructs a BuildingType object with a name, recipes it makes, and the cost to make the builindg
   *
   * @param name      is the name of the buildingType
   * @param recipes   is the Map of the names of the recipes it can make and the recipe to make it
   * @param cost      is the cost to make the building
   */
  public BuildingType(String name, Map<String, Recipe> recipes, Cost cost){
    this.name = name;
    this.recipes = recipes;
    this.cost = cost;
  }


  /**
   * Constructs a BuildingType object with a name, recipes it makes, and does not require resources to create
   *
   * @param name      is the name of the buildingType
   * @param recipes   is the Map of the names of the recipes it can make and the recipe to make it
   */
  public BuildingType(String name, Map<String, Recipe> recipes){
    this(name, recipes, new Cost());
  }


  /**
   * Adds the building type to the global list of building types
   *
   * @return the buildingType added to the list
   */
  public BuildingType register() {
      buildingTypeGlobalList.add(this);
      return this;
  }


  /**
   * Constructs a BuildingType object with a name, recipes it makes, and the cost to make the builindg
   *
   * @param productName    is the name of hte recipe to retrieve
   * @return Recip         the recipe of of the given productName
   */
  public Recipe getRecipeByProductName(String productName) {
    return recipes.get(productName);
  }


  public static List<BuildingType> buildingTypeGlobalList = new ArrayList<>();


  /**
   * Initializes the global list of BuildingType with given lsit of buildingTypes
   *
   * @return buildingTypeGlobalList    is the list of buildingTypes to initialize with
   */
  public static void setBuildingTypeGlobalList(List<BuildingType> buildingTypeGlobalList) {
    BuildingType.buildingTypeGlobalList = buildingTypeGlobalList;
  }


  /**
   * gets the list of builidingTypes in the game
   *
   * @retursn List<BuildingType>  is a list of all existing buildingTypes
   */
  public static List<BuildingType> getBuildingTypeGlobalList() {
    return buildingTypeGlobalList;
  }


  /**
   * Returns the string representation of building type
   *
   * @return  the string representation of the buidling type
   */
  @Override
  public String toString(){
    return "Factory Type\n{name='" + name +
           "',\n recipes=" + printRecipes() +
           "\n}";
  }


  //A helper function that returns a string representation of the recipes
  private String printRecipes() {
    StringBuilder result = new StringBuilder("[");

    if (recipes != null && !recipes.isEmpty()) {
      Iterator<Map.Entry<String, Recipe>> iterator = recipes.entrySet().iterator();

      while (iterator.hasNext()) {
        Recipe r = iterator.next().getValue();
        result.append(r.getOutput());

        if (iterator.hasNext()) {
          result.append(", ");
        }
      }
    }

    result.append("]");
    return result.toString();
  }


  /**
   * Retursn the name of the BuildingType
   */
  public String getName(){
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    BuildingType that = (BuildingType) o;
    return Objects.equals(name, that.name) && Objects.equals(recipes, that.recipes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, recipes);
  }


  /**
   * returns a Map of all recipes names with their respective Recipe
   *
   * @return  the Map of all recipes anmes with their Recipe
   */
  public Map<String, Recipe> getAllRecipes() {
    return this.recipes;
  }


  /**
   * Constructs a BuildingType object with a name, recipes it makes, and the cost to make the builindg
   *
   * @param name      is the name of the buildingType
   * @param recipes   is the Map of the names of the recipes it can make and the recipe to make it
   * @param cost      is the cost to make the building
   */
  public Cost getCost(){
    return this.cost;
  }
}
