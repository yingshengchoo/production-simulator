package productsimulation.request;

import productsimulation.Building;
import productsimulation.Recipe;

import java.util.Map;

public class Request {

  private static IdGenerator idGenerator = new IdGenerator();

  public static void clearIds() {
    idGenerator = new IdGenerator();
  }


  public int id;
  private final String ingredient;
  private final Recipe recipe;
  private final Building requester;
  private RequestStatus status;

  public Request(String ingredient, Recipe recipe, Building requester) {
    this.id = idGenerator.nextId();
    this.ingredient = ingredient;
    this.recipe = recipe;
    this.requester = requester;
    this.status = RequestStatus.WAITING;

  }

  public int getId() {
    return id;
  }

  /**
   * Updates the status of the request based on the available stock levels of ingredients.
   * If the status is already set to WORKING, the update will not proceed.
   * The method checks if the required ingredients for the recipe are available in
   * the specified stock. If any ingredient's quantity is insufficient, the status
   * is set to WAITING. If all required ingredients are available in sufficient
   * quantities, the status is set to READY.
   *
   * @param stock a map representing the available stock levels, where the key is the
   *              ingredient name and the value is the quantity available
   */
  public void updateStatus(Map<String, Integer> stock) {
      if (status == RequestStatus.WORKING) return;

      for (Map.Entry<String, Integer> ingredient : recipe.getIngredients().entrySet()) {
        int stockQuantity = stock.getOrDefault(ingredient.getKey(), 0);
        if (stockQuantity < ingredient.getValue()) {
          status = RequestStatus.WAITING;
          return;
        }
      }
      status = RequestStatus.READY;
  }

  public RequestStatus getStatus() {
    return status;
  }

  public int getLatency() {
    return recipe.getLatency();
  }
}
