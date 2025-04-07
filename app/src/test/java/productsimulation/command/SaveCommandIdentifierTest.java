package productsimulation.command;

import org.junit.jupiter.api.Test;
import productsimulation.command.command_identifier.CommandIdentifier;
import productsimulation.command.command_identifier.SaveCommandIdentifier;

import static org.junit.jupiter.api.Assertions.assertNull;

public class SaveCommandIdentifierTest {
    CommandIdentifier saveCommandIdentifier = new SaveCommandIdentifier(null);
    @Test
    public void testSaveCommand() {
        Command cmd = saveCommandIdentifier.checkFits("save ");
        assertNull(cmd);
    }
}
