package productionsimulation;

public abstract class InputRuleChecker {
  private final InputRuleChecker next;

  /**
   * Chains the different Input Rule Checkers
   *
   * @param next is the next input rule to be checked
   */
  public InputRuleChecker(InputRuleChecker next){
    this.next = next;
  }



  protected abstract String checkMyRule(/* add params here */);


  public String checkInput (/* add the params here*/) {
    String inputProblem = checkMyRule(/* add parameters here*/);
    //if we fail our own rule: stop the placement is not legal
    if (inputProblem != null) {
      return inputProblem;
    }
    //other wise, ask the rest of the chain.
    if (next != null) {
      return next.checkInput(/* add params here */);
    }
    //if there are no more rules, then the placement is legal
    return null;
  }

}
