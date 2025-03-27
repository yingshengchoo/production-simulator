package productsimulation.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetPolicyCommandIdentifierTest {
    private SetPolicyCommandIdentifier identifier = new SetPolicyCommandIdentifier(null);

    @Test
    void checkFits_valid() {
        Command cmd = identifier.checkFits("set policy request 'sjf' on 'D'");
        assertNotNull(cmd);
        assertTrue(cmd instanceof SetPolicyCommand);
        assertEquals( "sjf", ((SetPolicyCommand) cmd).getPolicyName());
        assertEquals("request", ((SetPolicyCommand) cmd).getPolicyType());

        cmd = identifier.checkFits("set policy request 'sjf' on default");
        assertEquals( "sjf", ((SetPolicyCommand) cmd).getPolicyName());
        assertEquals("request", ((SetPolicyCommand) cmd).getPolicyType());
    }

    @Test
    void checkFits_invalid() {
        SetPolicyCommandIdentifier identifier = new SetPolicyCommandIdentifier(null);
        Command cmd = identifier.checkFits("set policy request ");
        assertNull(cmd);

        cmd = identifier.checkFits("set policy requefst 'sjf' on default");
        assertNull(cmd);
    }
}