package productsimulation.model;

import productsimulation.Log;
import productsimulation.request.Request;
import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

public class Storage extends Building {

  private int priority;
  private int frequency;
  private int totalCapacity;
  private int remainingCapacity;
  private List<Request> readyQueue;

  /**
   * Constructs a Mine with the specified name, type, sources, and policies.
   *
   * @param name         is the name of the Mine.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Storage(String name, FactoryType type, List<Building> sources, int totalCapacity, int priority, SourcePolicy sourcePolicy, ServePolicy servePolicy){
    super(name, type, sources, sourcePolicy, servePolicy);
    this.totalCapacity = totalCapacity;
    this.priority = priority;
    this.remainingCapacity = totalCapacity;
    updateFrequency();
    readyQueue = new ArrayList<>();
  }

  /**
   * Constructs a Mine with the specified name, type, sources, and policies.
   *
   * @param name         is the name of the Mine.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Storage(String name, FactoryType type, Recipe storageType, int totalCapacity, int priority, SourcePolicy sourcePolicy, ServePolicy servePolicy){
        super(name, type, sourcePolicy, servePolicy);
        this.totalCapacity = totalCapacity;
        this.priority = priority;
        this.remainingCapacity = totalCapacity;
        updateFrequency();
  }

  /**
   * Updates the frequency of the request 
   */
  private void updateFrequency(){
    if(remainingCapacity == 0){
      return;
    }
    this.frequency = (int)Math.ceil((totalCapacity * totalCapacity) / (remainingCapacity * priority));
  }  

  
  @Override
  public boolean goOneStep(){
    for(Request request: readyQueue){
      
    }
    return false;
  }

  @Override
  public void addRequest(Request request){
    
  }
  
  @Override
  public String toString() {
    return "Storage\n{name='" + super.name + 
           "',\n type='" + super.type.getName() + 
           "',\n sources=" + printSources() + 
           // ",\n sourcePolicy=" + super.sourcePolicy.toString() + 
           // "',\n servePolicy=" + super.servePolicy.toString() + "'" +
      ",\n" + printStorageAndRequest()+
      "\n}";
  }
}
