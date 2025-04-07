package productsimulation.command;

import org.junit.jupiter.api.Test;
import productsimulation.command.command_identifier.RequestCommandIdentifier;

import static org.junit.jupiter.api.Assertions.*;

class RequestCommandIdentifierTest {
    @Test
    public void test_checkFits() {
        RequestCommandIdentifier identifier = new RequestCommandIdentifier(null);
        Command cmd = identifier.checkFits("request 'bolt' from 'best doors and bolts in town'");
        assertNotNull(cmd);
        assertTrue(cmd instanceof RequestCommand);
        assertEquals(((RequestCommand) cmd).getBuilding(), "best doors and bolts in town");
        assertEquals(((RequestCommand) cmd).getItem(), "bolt" );
    }

    @Test
    public void test_invalidCheckFits() {
        RequestCommandIdentifier identifier = new RequestCommandIdentifier(null);
        Command cmd = identifier.checkFits("request 'bolt' from 'best doors and bolts in town");
        assertNull(cmd);
        Command cmd2 = identifier.checkFits("request bolt' from 'best doors and bolts in town'");
        assertNull(cmd2);
    }
}