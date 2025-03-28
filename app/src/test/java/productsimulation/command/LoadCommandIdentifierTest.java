package productsimulation.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadCommandIdentifierTest {
    LoadCommandIdentifier loadCommandIdentifier = new LoadCommandIdentifier(null);

    @Test
    void checkFits_invalid() {
        Command cmd = loadCommandIdentifier.checkFits("load ");
        assertNull(cmd);
    }
}