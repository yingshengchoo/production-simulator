package productsimulation.command;

import java.io.BufferedReader;
import java.io.IOException;

public class CommandParser {
  private final CommandRuleChecker chainHead;

  public CommandParser() {
    // Build the chain in the constructor, just as SetupParser does with its input rule chain.
    // For groups of 3, you may omit the "set policy", "save", "load".
    chainHead =
            new RequestCommandChecker(
                    new StepCommandChecker(
                            new FinishCommandChecker(
                                    new VerboseCommandChecker(
                                            new SetPolicyCommandChecker(
                                                    new SaveCommandChecker(
                                                            new LoadCommandChecker(
                                                                    null)))))));
  }

  /**
   * parseLine calls the chainHead to parse a single line.
   *
   * @param line the user’s input line.
   * @return a Command if recognized, else null.
   */
  public Command parseLine(String line) {
    if (line == null) {
      return null;
    }
    return chainHead.checkInput(line.trim());
  }

  /**
   * parse method that reads from a BufferedReader line by line.
   */
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
        if (cmd instanceof FinishCommand) {
          System.out.println("Finish encountered; stopping command parsing.");
          break;
        }
      }
    }
  }
}
