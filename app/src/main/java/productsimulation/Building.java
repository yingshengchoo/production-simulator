package productsimulation;

import productsimulation.request.Request;
import productsimulation.request.ServePolicy;

import java.util.Queue;
import java.util.Map;
import java.util.List;

public abstract class Building{
  protected final String name;
  protected Queue<Request> requestQueue;
  protected Map<String, Integer> storage;
  protected List<Building> sources;
  //protected Logger logger;
  protected SourcePolicy sourcePolicy;
  protected ServePolicy servePolicy;

  /**
   * Constructs a Building with the specified name, type, sources, and policies.
   *
   * @param name         is the coordinate of the top left of the ship.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Building(String name, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy){
    this.name = name;
    this.sources = sources;
    this.sourcePolicy = sourcePolicy;
    this.servePolicy = servePolicy;
  }

  public void addRequest(){

  }

  public void goOneStep(){
  }

  public void updateBroadcast(){
  }

  public void changePolicy(){
  }

  public void notified(){
  }

  public void accept(){
  }
}
