package productsimulation.request;

import productsimulation.Log;
import productsimulation.LogicTime;
import productsimulation.model.Building;
import productsimulation.model.Recipe;
import productsimulation.model.drone.DronePort;
import productsimulation.model.road.Road;
import productsimulation.model.road.TransportQueue;
import productsimulation.model.GlobalStorage;
import productsimulation.model.waste.WasteDisposal;

import java.util.List;
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
  public int transLatency = 0;
  private Building worker;

  public Request(String ingredient, Recipe recipe, Building requester, int transLatency) {
    this(ingredient, recipe, requester);
    this.transLatency = transLatency;
  }

  public Request(String ingredient, Recipe recipe, Building requester) {
    this.id = idGenerator.nextId();
    this.ingredient = ingredient;
    this.recipe = recipe;
    this.requester = requester;
    if(recipe == null || recipe.getIngredients().isEmpty()) {
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
    // add waste to worker building
    if (worker != null && recipe != null && !recipe.getWasteMap().isEmpty()) {
      for (Map.Entry<String, Integer> e : recipe.getWasteMap().entrySet()) {
        String waste = e.getKey();
        Integer count = e.getValue();
        worker.addWaste(waste, count);
      }
    }

    if (requester != null) {
      Log.debugLog(ingredient + " produce done at " + LogicTime.getInstance().getStep());
      if (transLatency <= 0) {
        submitToRequester();
      } else {
        // 优先尝试drone配送
        DronePort port = DronePort.findEligiblePort(this, worker, this.requester);
        if (port != null) {
          return;
        }
        TransportQueue.addRequest(this);
      }
    } else {
      GlobalStorage.addItemToStorage(ingredient);
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

  public boolean isReadyToDeliver() {
    return this.transLatency <= 0;
  }

  public void decreaseTransLatency() {
    this.transLatency--;
  }

  public Building getRequester() {
    return requester;
  }

  public void setTransLatency(int transLatency) {
    this.transLatency = transLatency;
  }

  private static String validateProductionChain(Building b, Recipe r, String itemName) {
    List<Building> sources = b.getSources();
    Map<String, Integer> ingredients = r.getIngredients();

    for (String subItem : ingredients.keySet()) {
      boolean found = false;

      for (Building src : sources) {
        try {
          int dis = Road.getDistance(src, b); // 可达性检查，异常会中断流程
          if (src.canProduce(subItem)) {
            Recipe subRecipe = Recipe.getRecipe(subItem); // 获取 subItem 的配方
            if (subRecipe != null) {
              String result = validateProductionChain(src, subRecipe, subItem);
              if (result != null) {
                return result; // 下层原料链失败，直接返回
              }
            }
            found = true;
            break;
          }
        } catch (Exception e) {
          // 如果不可达，则忽略这个 source，尝试下一个
        }
      }

      if (!found) {
        return "The request chain cannot produce " + itemName;
      }
    }

    return null; // 所有原料都可以递归生产
  }


  public static String userRequestHandler(String itemName, String buildingName) {
    String err = null;
    try {
      Recipe r = Recipe.getRecipe(itemName);
      Building b = Building.getBuilding(buildingName);
      if(r != null && b != null) {
        // 1)建筑要有这个recipe
        if(!b.canProduce(itemName)) {
          return "The building cannot produce " + itemName;
        }
        // 2)建筑的source足够充分，即存在连通的source能提供所需的每一种原料，且递归后仍充分
        err = validateProductionChain(b, r, itemName);

        // 3)建筑要和waste连接
        // 目前不连也能开造，不过造了一会就会堵住，堵住之后再连waste可以继续生产，这点和真实游戏一样

        Request request = new Request(itemName, r, null);
        b.addRequest(request);
      }
      return err;
    } catch (Exception e) {
      return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
  }

    public void setWorker(Building worker) {
        this.worker = worker;
    }


    public void submitToRequester() {
      if (this instanceof WasteRequest) {
        // 提交waste
        WasteRequest wasteRequest = (WasteRequest) this;
        WasteDisposal wasteDisposal = (WasteDisposal) requester;
        wasteDisposal.commitWaste(wasteRequest.getIngredient(), wasteRequest.getCount());
      } else {
        // 提交普通request
        requester.updateStorage(getIngredient());
      }
    }
}
