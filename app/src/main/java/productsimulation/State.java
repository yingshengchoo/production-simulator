package productsimulation;

import java.io.*;
import java.util.List; 

public class State implements Serializable{
  //private ByteArrayInputStream serializationData;

  private List<Building> buildings;
  private List<Recipe> recipes;
  private List<FactoryType> types;
  
  /**
   * Checks to see if the filename is valid(doesn't contain any special characters).
   *
   * @param buildings     is the list of buildings in the simulation. 
   * @param types         is the list types of Factory in the simulation.
   * @param recipes       is the list of recipes in the simulation.
   */
  public State(List<Building> buildings, List<FactoryType> types, List<Recipe> recipes){
    this.buildings = buildings;
    this.types = types;
    this.recipes = recipes;
    
  }

  /**
   * Checks to see if the filename is valid(doesn't contain any special characters).
   *
   * @param filename is the name of the filename to check. 
   * @return true if the filename is valid; false otherwise.
   */
  public boolean checkFilename(String filename) {
    if (filename == null || filename.isEmpty()) {
      return false;
    }
        
    String illegalChars = "\\/:*?\"<>|";
    for (char c : illegalChars.toCharArray()) {
      if (filename.indexOf(c) >= 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Saves the Current State in "filename".ser. Filename must not contain special characters
   *
   * @param filename is the name of the filename to save simluation data to. 
   */  
  public void save(String filename){

    if(!checkFilename(filename)){
      throw new IllegalArgumentException("Invalid Filename. Filename must not contain any special characters");
    }
    
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("SavedStates/" + filename + ".ser"))) {
      out.writeObject(this);
      System.out.println("State saved to SavedStates/" + filename + ".ser");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads a previously saved state from a file with the given filename.
   *
   * @param filename is the name of the file to load the state from.
   */    
  public void load(String filename){
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("SavedStates/" + filename + ".ser"))) {
      State loadedState = (State) in.readObject();
      this.buildings = loadedState.buildings;
      this.recipes = loadedState.recipes;
      this.types = loadedState.types;
      System.out.println("State loaded from SavedStates/" + filename + ".ser");
    } catch (FileNotFoundException e) {
       System.out.println("Error: The file data " + filename + " does not exist.");
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void visitBuilding(){
    BuildingUpdateVisitor visitor = new BuildingUpdateVisitor();
    for (Building b : buildings) {
      b.accept(visitor); 
    }
  }

  /**
   * Displays the current state of the simulation
   *
   * @param o is the PrintStream to print the current state.
   */
  public void showState(PrintStream o){
    o.println("Current State Information:\n");
    printRecipes(o);
    printTypes(o);
    printBuildings(o);
    
  }
  
  /**
   * Displays the current buildings in the current state of the simulation
   *
   * @param o is the PrintStream to print the current state.
   */
  public void printBuildings(PrintStream o){
    o.println("Buildings:");
    for(Building b: buildings){
      o.println(b.toString());
    }
  }
  
  /**
   * Displays the current Factory Types in the current state of the simulation
   *
   * @param o     is the PrintStream to print the current state.
   */
  public void printTypes(PrintStream o){
    o.println("Factory Types:");
    for(FactoryType t : types){
      o.println(t.toString());
    }
  }

  /**
   * Displays the current Recipes in the current state of the simulation
   *
   * @param o is the PrintStream to print the current state.
   */
  public void printRecipes(PrintStream o){
    o.println("Recipes:");
    for(Recipe r : recipes){
      o.println(r.toString());
    }
  }
}
