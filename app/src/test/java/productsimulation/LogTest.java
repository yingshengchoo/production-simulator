package productsimulation;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

// see "Verbose" command in specification for details
// 0: only completion of user request
// 1: 0 + ...
// 2: 1 + ...
// >=3: debug
public class LogTest {
    private String filePathStr = "src/test/resources/test.log";
    private Path filePath = Paths.get(filePathStr);

    public void cleanUpLogFile() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
            Files.write(filePath, "".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Error when writing to" + filePathStr);
        }
    }

    public String getActualLogFromFile() {
        try (InputStream actualOutputStream = Files.newInputStream(filePath)) {
            return new String(actualOutputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error when reading " + filePathStr);
        }
        return "";
    }

    public void logTestHelper(String expected) {
        cleanUpLogFile();
        writeSomeLog();
        String actual = getActualLogFromFile();
        assertEquals(expected, actual);
    }

    private void writeSomeLog() {
        Log.level0Log("level0 log");
        Log.level1Log("level1 log");
        Log.level2Log("level2 log");
        Log.debugLog("debug log");
    }

    @Test
    void test_special() {
        // larger than 3, the same as 3.
        Log.setLogLevel(4);
        String expected = "[Log] - level0 log\n" +
                "[Log] - level1 log\n" +
                "[Log] - level2 log\n" +
                "[Log] - debug log\n";
        logTestHelper(expected);
        // smaller than 0, no effect.
        Log.setLogLevel(-1);
        expected = "[Log] - level0 log\n" +
                "[Log] - level1 log\n" +
                "[Log] - level2 log\n" +
                "[Log] - debug log\n";
        logTestHelper(expected);
    }

    @Test
    void test_debugLog() {
        Log.setLogLevel(3);
        String expected = "[Log] - level0 log\n" +
                "[Log] - level1 log\n" +
                "[Log] - level2 log\n" +
                "[Log] - debug log\n";
        logTestHelper(expected);
    }

    @Test
    void test_level2Log() {
        Log.setLogLevel(2);
        String expected = "[Log] - level0 log\n" +
                "[Log] - level1 log\n" +
                "[Log] - level2 log\n";
        logTestHelper(expected);
    }

    @Test
    void test_level1Log() {
        Log.setLogLevel(1);
        String expected = "[Log] - level0 log\n" +
                "[Log] - level1 log\n";
        logTestHelper(expected);
    }

    @Test
    void test_level0Log() {
        Log.setLogLevel(0);
        String expected = "[Log] - level0 log\n";
        logTestHelper(expected);
    }
}