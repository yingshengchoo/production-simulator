package productsimulation.request;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.request.sourcePolicy.SourcePolicy;

import java.util.List;
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
  private int remainTime;
  private RequestStatus status;

  public Request(String ingredient, Recipe recipe, Building requester) {
    this.id = idGenerator.nextId();
    this.ingredient = ingredient;
    this.recipe = recipe;
    this.requester = requester;
    this.remainTime = recipe.getLatency();
    this.status = RequestStatus.WAITING;
  }

  /**
   * Constructs and returns a new {@code Request} object. The method uses the given parameters
   * to identify a suitable {@code Building} from the provided list using the {@code SourcePolicy},
   * and retrieves the corresponding {@code Recipe} for the specified item from the recipe list.
   * The created Request is then added to the identified Building's request queue.
   *
   * @param item the name of the item for which the request is being created
   * @param sourcePolicy the policy used to determine the optimal {@code Building} from the list
   * @return the newly constructed {@code Request} containing the specified item, recipe, and building
   * @throws IllegalArgumentException if no suitable {@code Building} is found in the {@code buildingList}
   */
  public static Request BuildRequest(String item, SourcePolicy sourcePolicy,
                                     Map<String, Building> buildingMap, Map<String, Recipe> recipeMap ) {
    Building building = sourcePolicy.getSource(buildingMap.values().stream().toList());
    if (building == null) {
      throw new IllegalArgumentException("ERROR: 0 Building in list!");
    }
    Recipe recipe = recipeMap.get(item);
    Request request = new Request(item, recipe, building);
    building.addRequest(request);
    return request;
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

  public void readyToWorking() {
    if (status == RequestStatus.READY) {
      status = RequestStatus.WORKING;
    }
  }

  public RequestStatus getStatus() {
    return status;
  }

  public int getLatency() {
    return recipe.getLatency();
  }
  public int getRemainTime() { return remainTime; }
  public void remainTimeMinusOne() { remainTime -= 1; }
  public void doneReportAndTransport() {
    if(requester != null) {
      requester.updateStorage(ingredient);
    } else {
      Log.debugLog("user request is done: " + ingredient + " at " + LogicTime.getInstance().getStep());
    }
  }

  public String getIngredient() { return ingredient; }
}
