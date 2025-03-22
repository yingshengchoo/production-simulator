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

  public void parse(BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) {
        continue;
      }
      Command cmd = parseLine(line);
      if (cmd == null) {
        System.err.println("Unrecognized command: " + line);
      } else {
        cmd.execute();
      }
    }
  }
}
