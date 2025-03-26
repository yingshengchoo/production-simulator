package productsimulation.command;

import org.junit.jupiter.api.Test;

import productsimulation.LogTest;

class VerboseCommandTest {
    @Test
    void test_basic() {
        LogTest logTest = new LogTest();
        VerboseCommand verboseCommand = new VerboseCommand(1);
        verboseCommand.execute();
        String expected = "[Log] - level0 log\n" +
                "[Log] - level1 log\n";;
        logTest.logTestHelper(expected);
    }

}