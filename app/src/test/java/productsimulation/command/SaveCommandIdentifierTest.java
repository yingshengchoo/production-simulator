package productsimulation.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class SaveCommandIdentifierTest {
    SaveCommandIdentifier saveCommandIdentifier = new SaveCommandIdentifier(null);
    @Test
    public void testSaveCommand() {
        Command cmd = saveCommandIdentifier.checkFits("save ");
        assertNull(cmd);
    }
}
