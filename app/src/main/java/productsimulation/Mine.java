package productionsimulation;

import java.util.List;

public class Mine extends Building {
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
}
