package productsimulation.command;

import org.junit.jupiter.api.Test;
import productsimulation.LogicTime;
import productsimulation.State;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LoadCommandTest {

    @Test
    void execute() {
        State.initialize(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), LogicTime.getInstance());
        String filename = "testSave";
        File file = new File("SavedStates/" + filename + ".ser");
        assertTrue(file.exists(), "File should exist after saving state.");
//        assertDoesNotThrow(() -> State.getInstance().save(filename));

        LoadCommand cmd = new LoadCommand(filename);
        assertDoesNotThrow(() -> cmd.execute());
    }

}