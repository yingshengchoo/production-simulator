package productsimulation.command;

import productsimulation.command.command_identifier.*;

public class CommandParser {
  private final CommandIdentifier commandIdentifier;

  public CommandParser() {
    commandIdentifier =
            new RequestCommandIdentifier(
                    new StepCommandIdentifier(
                            new FinishCommandIdentifier(
                                    new VerboseCommandIdentifier(
                                            new SetPolicyCommandIdentifier(
                                                    new SaveCommandIdentifier(
                                                            new LoadCommandIdentifier(
                                                                    new ConnectCommandIdentifier(
                                                                            new DisconnectCommandIdentifier(
                                                                                    new RemoveBuildingCommandIdentifier(
                                                                                            null
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
  }

  public Command parseLine(String line) {
    if (line == null) {
      return null;
    }
    return commandIdentifier.checkInput(line.trim());
  }
}
