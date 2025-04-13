package productsimulation.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import productsimulation.model.road.Road;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectCommandTest {
    @Test
    void testConnectCommand() {
        ConnectCommand cmd = new ConnectCommand("a", "b");
        assertEquals("a", cmd.getSource());
        assertEquals("b", cmd.getDestination());
    }
}
