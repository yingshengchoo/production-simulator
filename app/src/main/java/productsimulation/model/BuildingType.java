package productsimulation.model;

import java.util.*;
import java.io.Serializable;


public class BuildingType implements Serializable {
  private final String name;
  private Map<String, Recipe> recipes;


  public BuildingType(String name, Map<String, Recipe> recipes){
    this.name = name;
    this.recipes = recipes;
  }

  public Recipe getRecipeByProductName(String productName) {
    return recipes.get(productName);
  }

  public static List<BuildingType> buildingTypeList;

  public static void setBuildingTypeList(List<BuildingType> buildingTypeList) {
    BuildingType.buildingTypeList = buildingTypeList;
  }

  public static List<BuildingType> getBuildingTypeList() {
    return buildingTypeList;
  }
  
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

    public Map<String, Recipe> getAllRecipes() {
        return this.recipes;
    }
}
