package productsimulation.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public  class CommandParserTest {

    @Test
    public void test_request_valid() {
        CommandParser parser = new CommandParser();
        Command cmd = parser.parseLine("request 'widget' from 'WidgetFactory'");
        assertNotNull(cmd);
        assertTrue(cmd instanceof RequestCommand);
        RequestCommand rc = (RequestCommand) cmd;
        assertEquals("widget", rc.getItem());
        assertEquals("WidgetFactory", rc.getBuilding());
    }

    @Test
    public void test_request_invalid() {
        CommandParser parser = new CommandParser();
        // Missing quotes
        Command cmd = parser.parseLine("request widget from WidgetFactory");
        assertNull(cmd);
    }

    @Test
    public void test_step_valid() {
        CommandParser parser = new CommandParser();
        Command cmd = parser.parseLine("step 10");
        assertNotNull(cmd);
        assertTrue(cmd instanceof StepCommand);
        assertEquals(10, ((StepCommand) cmd).getSteps());
    }

    @Test
    public void test_step_invalid() {
        CommandParser parser = new CommandParser();
        Command cmd = parser.parseLine("step 0"); // invalid step number
        assertNull(cmd);
    }

    @Test
    public void test_finish() {
        CommandParser parser = new CommandParser();
        Command cmd = parser.parseLine("finish");
        assertNotNull(cmd);
        assertTrue(cmd instanceof FinishCommand);
    }

    @Test
    public void test_verbose_valid() {
        CommandParser parser = new CommandParser();
        Command cmd = parser.parseLine("verbose 2");
        assertNotNull(cmd);
        assertTrue(cmd instanceof VerboseCommand);
        VerboseCommand vc = (VerboseCommand) cmd;
        assertEquals(2, vc.getLevel());
    }

    @Test
    public void test_setPolicy_valid() {
        CommandParser parser = new CommandParser();
        String line = "set policy request 'sjf' on 'my factory'";
        Command cmd = parser.parseLine(line);
        assertNotNull(cmd);
        assertTrue(cmd instanceof SetPolicyCommand);
        SetPolicyCommand spc = (SetPolicyCommand) cmd;
        assertEquals("request", spc.getTypeField());
        assertEquals("'sjf'", spc.getPolicy());
        assertEquals("'my factory'", spc.getTarget());
    }

    @Test
    public void test_save_valid() {
        CommandParser parser = new CommandParser();
        Command cmd = parser.parseLine("save myState.json");
//        assertNotNull(cmd);
//        assertTrue(cmd instanceof SaveCommand);
        SaveCommand sc = (SaveCommand) cmd;
//        assertEquals("myState.json", sc.getFilename());
    }

    @Test
    public void test_load_valid() {
        CommandParser parser = new CommandParser();
        Command cmd = parser.parseLine("load myState.json");
        assertNotNull(cmd);
        assertTrue(cmd instanceof LoadCommand);
        LoadCommand lc = (LoadCommand) cmd;
        assertEquals("myState.json", lc.getFilename());
    }

    @Test
    public void test_unknownCommand() {
        CommandParser parser = new CommandParser();
        Command cmd = parser.parseLine("do something else entirely");
        assertNull(cmd);
    }
}
