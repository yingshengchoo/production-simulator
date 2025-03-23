package productsimulation.command;

import java.io.BufferedReader;
import java.io.IOException;

public class CommandParser {
  private final CommandIdentifier ruleIdentifier;

  public CommandParser() {
    ruleIdentifier =
            new RequestCommandIdentifier(
                    new StepCommandIdentifier(
                            new FinishCommandIdentifier(
                                    new VerboseCommandIdentifier(
                                            new SetPolicyCommandIdentifier(
                                                    new SaveCommandIdentifier(
                                                            new LoadCommandIdentifier(
                                                                    null)))))));
  }

  public Command parseLine(String line) {
    if (line == null) {
      return null;
    }
    return ruleIdentifier.checkInput(line.trim());
  }
}
