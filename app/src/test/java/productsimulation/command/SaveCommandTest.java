package productsimulation.command;

import org.junit.jupiter.api.Test;
import productsimulation.LogicTime;
import productsimulation.State;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SaveCommandTest {
    @Test
    void execute() {
        State.initialize(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), LogicTime.getInstance());
        SaveCommand cmd = new SaveCommand("testSave");
        assertDoesNotThrow(() -> cmd.execute());
    }

}