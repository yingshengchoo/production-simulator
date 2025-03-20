package productsimulation.model;

import productsimulation.request.ServePolicy;
import productsimulation.sourcePolicy.SourcePolicy;

import java.util.List;

public class Factory extends Building {
  /**
   * Constructs a Factory with the specified name, type, sources, and policies.
   *
   * @param name         is the coordinate of the top left of the ship.
   * @param type         is the Building Type.
   * @param sources      is list of buildings that provides the ingredients to make the recipes.
   * @param sourcePolicy is the policy that the building uses to select between sources.
   * @param servePolicy  is the policy that the building uses to select between requests.
   */
  public Factory(String name, FactoryType type, List<Building> sources, SourcePolicy sourcePolicy, ServePolicy servePolicy){
    super(name, type, sources, sourcePolicy, servePolicy);
  }
}

