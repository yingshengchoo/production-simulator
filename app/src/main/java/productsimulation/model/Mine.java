package productsimulation.model;

import productsimulation.request.servePolicy.ServePolicy;
import productsimulation.request.sourcePolicy.SourcePolicy;
import java.io.Serializable;
import java.util.List;

public class Mine extends Building implements Serializable {
  /**
   * Constructs a Mine with the specified name, type, sources, and policies.
   *
   * @param name         is the coordinate of the top left of the ship.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Mine(String name, FactoryType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy){
    super(name, type, sources, sourcePolicy, servePolicy);
  }

    public Mine(String name, FactoryType type,  SourcePolicy sourcePolicy, ServePolicy servePolicy){
        super(name, type, sourcePolicy, servePolicy);
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
      "',\n mine='" + super.type.getName() + 
           "',\n sources=" + printSources() + 
           // ",\n sourcePolicy=" + super.sourcePolicy.toString() + 
           // ",\n servePolicy=" + super.servePolicy.toString() + "'" + 
           "\n}";
  }

  /**
   * Gets the name of the Mine
   *
   * @return    the name of the mine
   */
  public String getName(){
    return name;
  }

  @Override
  public void accept(BuildingVisitor visitor) {
    visitor.visit(this);
  }
}
