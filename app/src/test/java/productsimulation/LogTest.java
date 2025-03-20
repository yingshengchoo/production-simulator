package productsimulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

// see "Verbose" command in specification for details
// 0: only completion of user request
// 1: 0 + ...
// 2: 1 + ...
// >=3: debug
class LogTest {
    private void testHelper(String expected) {
        try {
            // 清空文件内容
            Path filePath = Paths.get("src/test/resources/test.log");
            Files.write(filePath, "".getBytes(StandardCharsets.UTF_8));

            logTestHelper();

            try (InputStream actualOutputStream = Files.newInputStream(filePath)) {
                String actual = new String(actualOutputStream.readAllBytes(), StandardCharsets.UTF_8);
                assertEquals(expected, actual);
            }
        } catch (IOException e) {
            System.err.println("Error when reading or writing to src/test/resources/test.log");
            fail("Test failed due to I/O error");
        }
    }

    private void logTestHelper() {
        Log.level0Log("level0 log");
        Log.level1Log("level1 log");
        Log.level2Log("level2 log");
        Log.debugLog("debug log");
    }

    @Test
    void test_special() {
        // larger than 3, the same as 3.
        Log.setLogLevel(4);
        String expected = "level0 log\n" +
                "level1 log\n" +
                "level2 log\n" +
                "debug log\n";
        testHelper(expected);
        // smaller than 0, no effect.
        Log.setLogLevel(-1);
        expected = "level0 log\n" +
                "level1 log\n" +
                "level2 log\n" +
                "debug log\n";
        testHelper(expected);
    }

    @Test
    void test_debugLog() {
        Log.setLogLevel(3);
        String expected = "level0 log\n" +
                "level1 log\n" +
                "level2 log\n" +
                "debug log\n";
        testHelper(expected);
    }

    @Test
    void test_level2Log() {
        Log.setLogLevel(2);
        String expected = "level0 log\n" +
                "level1 log\n" +
                "level2 log\n";
        testHelper(expected);
    }

    @Test
    void test_level1Log() {
        Log.setLogLevel(1);
        String expected = "level0 log\n" +
                "level1 log\n";
        testHelper(expected);
    }

    @Test
    void test_level0Log() {
        Log.setLogLevel(0);
        String expected = "level0 log\n";
        testHelper(expected);
    }
}