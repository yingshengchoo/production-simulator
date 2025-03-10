package productionsimulation;

public enum RequestStatus {
  WAITING, WORKING, READY;
}

public class Request {

  public int id;
  private final String ingredient;
  private final Recipe recipe;
  private final Building requester;
  private RequestStatus status;

  public Request(String ingredient, Recipe recipe, Building requester) {
    this.ingredient = ingredient;
    this.recipe = recipe;
    this.requester = requester;
    this.status = RequestStatus.WAITING;
  }

  public static int nextRequestId() {
    //TODO
    return 0;
  }
}
