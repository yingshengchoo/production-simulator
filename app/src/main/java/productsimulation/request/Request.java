package productsimulation.request;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.model.Building;
import productsimulation.model.Recipe;
import java.util.Map;

import java.io.Serializable;

public class Request implements Serializable{

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
    if(recipe.getIngredients().isEmpty()) {
      this.status = RequestStatus.READY;
    } else {
      this.status = RequestStatus.WAITING;
    }
  }

  private Request(String ingredient, Building requester, boolean dummy) {
    this.id = dummy ?  -1 : idGenerator.nextId();
    this.ingredient = ingredient;
    this.recipe = Recipe.getRecipe(ingredient);
    this.requester = requester;
    this.status = recipe.getIngredients().isEmpty() ? RequestStatus.READY : RequestStatus.WAITING;
  }

  public static Request getDummyRequest(String ingredient, Building requester) {
    return new Request(ingredient, requester, true);
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
      updateStatus("stock_owner_name_placeholder", false, stock);
  }

  public void updateStatus(String stockOwnerName, boolean newIngredientArrived, Map<String, Integer> stock) {
    if (status == RequestStatus.WORKING) return;

    boolean isReady = true;
    for (Map.Entry<String, Integer> ingredient : recipe.getIngredients().entrySet()) {
      int stockQuantity = stock.getOrDefault(ingredient.getKey(), 0);
      if (stockQuantity < ingredient.getValue()) {
        isReady = false;
      }
      if(newIngredientArrived) {
        Log.level2Log("    " + stockOwnerName + " need " +
                stockQuantity + "/" + ingredient.getValue() + " " + ingredient.getKey()
                + " for " + recipe.getOutput());
      }
    }

    if(isReady) {
      status = RequestStatus.READY;
    } else {
      status = RequestStatus.WAITING;
    }
  }

  public void readyToWorking(Map<String, Integer> stock) {
    // initial state is ready, so no need to check stock in this function
    if (status == RequestStatus.READY) {
      // deduct the inventory
      for (Map.Entry<String, Integer> ingredient : recipe.getIngredients().entrySet()) {
        String ingredientName = ingredient.getKey();
        int ingredientOnNeed = ingredient.getValue();
        stock.put(ingredientName, stock.get(ingredientName) - ingredientOnNeed);
      }
      // status transition
      status = RequestStatus.WORKING;
    }
  }
  
  public RequestStatus getStatus() {
    return status;
  }

  public int getLatency() {
    return recipe.getLatency();
  }

  public void doneReportAndTransport() {
    if(requester != null) {
      requester.updateStorage(ingredient);
    } else {
//      [order complete] Order 0 completed (door) at time 21
      Log.level0Log("[order complete] Order " + id + " completed (" + ingredient + ")" +
              " at time " + LogicTime.getInstance().getStep());
    }
  }

  public String getIngredient() { return ingredient; }

  public String getRequesterName() {
    if(requester == null) {
      return "user";
    }
    return requester.getName();
  }

  public Recipe getRecipe() { return recipe; }

  public boolean isSameItemRequester(Request request) {
    if(requester == null) {
      return ingredient.equals(request.ingredient);
    }
    return ingredient.equals(request.ingredient) && requester.equals(request.requester);
  }
}
