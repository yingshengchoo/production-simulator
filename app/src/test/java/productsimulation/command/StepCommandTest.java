package productsimulation.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StepCommandTest {
    @Test
    void execute() {
        StepCommand cmd = new StepCommand(10);
        cmd.execute();
        assertEquals(10, cmd.getSteps());
    }
}