package productsimulation;

import productsimulation.request.ServePolicy;
import java.io.Serializable;
import java.util.List;

public class Mine extends Building implements Serializable {
  String mine;
  /**
   * Constructs a Mine with the specified name, type, sources, and policies.
   *
   * @param name         is the coordinate of the top left of the ship.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Mine(String name, String mine, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy){
    super(name, sources, sourcePolicy, servePolicy);
    this.mine = mine;
  }

  //A helper function that returns a string representation of the sources.
  private String printSources() {
    StringBuilder result = new StringBuilder("[");
    if (super.sources != null && !super.sources.isEmpty()) {
        for (int i = 0; i < super.sources.size(); i++) {
            Building b = super.sources.get(i);
            result.append(b.getName());
           
            if (i < super.sources.size() - 1) {
                result.append(", ");
            }
        }
    }
    
    result.append("]");  
    return result.toString();
  }

  // Returns the String representation of Mine.
  @Override
  public String toString() {
    return "Mine\n{name='" + super.name + 
           "',\n mine='" + mine + 
           "',\n sources=" + printSources() + 
           // ",\n sourcePolicy=" + super.sourcePolicy.toString() + 
           // ",\n servePolicy=" + super.servePolicy.toString() + "'" + 
           "\n}";
  }

  public String getName(){
    return name;
  }
}
