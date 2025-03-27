
package productsimulation;

import productsimulation.model.*;
import productsimulation.request.servePolicy.FIFOPolicy;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import productsimulation.request.sourcePolicy.SourceQLen;

import java.io.*;
import java.util.List; 

public class State implements Serializable{

  private static State instance;
  
  private List<Building> buildings;
  private List<Recipe> recipes;
  private List<FactoryType> types;
  private RequestBroadcaster requestbroadcaster;
  private LogicTime logictime;
  private SourcePolicy defaultSourcePolicy;
  private ServePolicy defaultServePolicy;


  /**
   * Constructs a State object class which represents the current state of the simulation.
   *
   * @param buildings          is the list of buildings in the simulation. 
   * @param types              is the list types of Factory in the simulation.
   * @param recipes            is the list of recipes in the simulation.
   * @param requestbroadcaster is the requestbroadcaster used in the simulation
   * @param logictime          is the logictime used in the simulation. 
   */
  private State(List<Building> buildings, List<FactoryType> types, List<Recipe> recipes, RequestBroadcaster requestbroadcaster, LogicTime logictime) {
    this.buildings = buildings;
    this.types = types;
    this.recipes = recipes;
    this.requestbroadcaster = requestbroadcaster;
    this.logictime = logictime;
    this.defaultSourcePolicy = new SourceQLen();
    this.defaultServePolicy = new FIFOPolicy();

    for(Building b: buildings) {
      b.changeSourcePolicy(defaultSourcePolicy);
      b.changeServePolicy(defaultServePolicy);
    }
  }

  /**
   * Initializes the State instance.
   *
   * @param buildings     is the list of buildings in the simulation. 
   * @param types         is the list types of Factory in the simulation.
   * @param recipes       is the list of recipes in the simulation.
   */
  public static void initialize(List<Building> buildings, List<FactoryType> types, List<Recipe> recipes, RequestBroadcaster requestbroadcaster, LogicTime logictime) {
    if (instance == null) {
      instance = new State(buildings, types, recipes, requestbroadcaster, logictime);
    }
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
   * Returns the state of the current program.
   *
   * @throws IllegalStateException  if State has not been initialized.
   */
  public static State getInstance() {
    if (instance == null) {
      throw new IllegalStateException("State has not been initialized.");
    }
      return instance;
  }

  /**
   * Saves the Current State in "filename".ser. Filename must not contain special characters
   *
   * @param filename                   the name of the file (without extension) to save in the "SavedStates/" directory.
   * @throws IllegalArgumentException  if the filename contains illegal characters.
   * @throws IOException               if an I/O error occurs while writing the file.
   */  
  public void save(String filename) throws FileNotFoundException, IOException {

    if(!checkFilename(filename)){
      throw new IllegalArgumentException("Invalid Filename. Filename must not contain any special characters");
    }

    File dir = new File("SavedStates");
    if (!dir.exists()) {
       dir.mkdirs();
    }
    
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("SavedStates/" + filename + ".ser"))) {
      out.writeObject(this);
      System.out.println("State saved to SavedStates/" + filename + ".ser");
    } 
  }

  /**
   * Loads a previously saved state from a file with the given filename.
   *

   * @param filename                the name of the file (without extension) to load from the "SavedStates/" directory.
   * @throws IOException            if an I/O error occurs while reading the file.
   * @throws FileNotFoundException  if file does not exist within the SaveStates directory
   * @throws ClassNotFoundException if serialized object class cannot be found.
   */      
  public void load(String filename) throws IOException, FileNotFoundException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("SavedStates/" + filename + ".ser"))) {
      State loadedState = (State) in.readObject();
      this.buildings = loadedState.buildings;
      this.recipes = loadedState.recipes;
      this.types = loadedState.types;
      this.requestbroadcaster = loadedState.requestbroadcaster;
      this.logictime = loadedState.logictime;

      StateLoadVisitor visitor = new StateLoadVisitor();
      loadedState.logictime.accept(visitor);
      loadedState.requestbroadcaster.accept(visitor);

      
      System.out.println("State loaded from SavedStates/" + filename + ".ser");
      showState(System.out);
    } 
  } 

  /**
   * Displays the current state of the simulation
   *
   * @param o is the PrintStream to print the current state.
   */
  public void showState(PrintStream o){
    o.println("Current State Information:");
    printLogicTime(o);
    printList("Recipes:", recipes, o);
    printList("Factory Types:", types, o);
    printList("Buildings:", buildings, o);
  }

  /**
   * Displays the current Time Step of the simulation
   *
   * @param o is the PrintStream to print the current state.
   */
  public void printLogicTime(PrintStream o){
    o.println(logictime.toString());
  }
  /**
   * Displays the current object in the current state of the simulation
   *
   * @param o is the PrintStream to print the current state.
   */
  public void printList(String title, List<?> list, PrintStream o) {
    o.println(title);
    for (Object item : list) {
        o.println(item.toString());
    }
  }

  
  //Resets State. Used for Testing purposes only
  public void reset(){
    this.buildings = null;
    this.types = null;
    this.recipes = null;
    this.requestbroadcaster = null;
    this.logictime = null;
  }

  public void setInstanceToNull(){
    instance = null;
  }

  public Building getBuilding(String name) {
    for (Building b : buildings) {
      if (b.getName().equals(name)) {
        return b;
      }
    }
    return null;
  }

  public List<Building> getBuilding() {
    return buildings;
  }

  public ServePolicy getDefaultServePolicy() {
    return defaultServePolicy;
  }

  public SourcePolicy getDefaultSourcePolicy() {
    return defaultSourcePolicy;
  }

  public void setDefaultSourcePolicy(SourcePolicy defaultSourcePolicy) {
    this.defaultSourcePolicy = defaultSourcePolicy;
  }

  public void setDefaultServePolicy(ServePolicy defaultServePolicy) {
    this.defaultServePolicy = defaultServePolicy;
  }

}


