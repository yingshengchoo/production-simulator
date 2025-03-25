package productsimulation.command;

import java.io.IOException;

public abstract class Command {
  public Command() {}

  public abstract void execute() throws IOException, ClassNotFoundException;
}
