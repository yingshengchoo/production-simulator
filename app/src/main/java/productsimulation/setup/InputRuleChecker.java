package productsimulation.setup;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class InputRuleChecker {
  private final InputRuleChecker next;

  /**
   * Chains the different Input Rule Checkers.
   *
   * @param next is the next input rule to be checked.
   */
  public InputRuleChecker(InputRuleChecker next){
    this.next = next;
  }

  /**
   * Each concrete rule checker implements its own check.
   *
   * @param root the root JsonNode representing the whole input JSON.
   * @return null if the rule passes, or an error message if it fails.
   */
  protected abstract String checkMyRule(JsonNode root);

  /**
   * Checks the input by calling the current rule then the rest of the chain.
   *
   * @param root the root JsonNode.
   * @return null if all rules pass; otherwise, returns the first encountered error message.
   */
  public String checkInput(JsonNode root) {
    String inputProblem = checkMyRule(root);
    if (inputProblem != null) {
      return inputProblem;
    }
    if (next != null) {
      return next.checkInput(root);
    }
    return null;
  }
}
