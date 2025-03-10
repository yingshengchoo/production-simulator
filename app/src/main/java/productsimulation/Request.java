package productionsimulation;

public class Request {

  enum RequestStatus {
    WAITING, WORKING, READY;
  }

  public int id;
  private final String ingredient;
  private final Recipe recipe;
  private final Building requester;
  private RequestStatus status;

  public Request(String ingredient, Recipe recipe, Building requester){
    this.ingredient = ingredient;
    this.recipe = recipe;
    this.requester = requester;
  }

  public static int nextRequestId(){
    return 0;
  }
}
