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
    public void logTestHelper(String expected) {
        Path filePath = Paths.get("src/test/resources/test.log");
        try {
            // 清空文件内容
            if (!Files.exists(filePath)) {
                // 创建父目录（如果不存在）
                Files.createDirectories(filePath.getParent());
                // 创建文件
                Files.createFile(filePath);
            }
            Files.write(filePath, "".getBytes(StandardCharsets.UTF_8));

            writeSomeLog();

            try (InputStream actualOutputStream = Files.newInputStream(filePath)) {
                String actual = new String(actualOutputStream.readAllBytes(), StandardCharsets.UTF_8);
                assertEquals(expected, actual);
            }
        } catch (IOException e) {
            System.err.println("Error when reading or writing to src/test/resources/test.log");
            fail("Test failed due to I/O error");
        }
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
        String expected = "level0 log\n" +
                "level1 log\n" +
                "level2 log\n" +
                "debug log\n";
        logTestHelper(expected);
        // smaller than 0, no effect.
        Log.setLogLevel(-1);
        expected = "level0 log\n" +
                "level1 log\n" +
                "level2 log\n" +
                "debug log\n";
        logTestHelper(expected);
    }

    @Test
    void test_debugLog() {
        Log.setLogLevel(3);
        String expected = "level0 log\n" +
                "level1 log\n" +
                "level2 log\n" +
                "debug log\n";
        logTestHelper(expected);
    }

    @Test
    void test_level2Log() {
        Log.setLogLevel(2);
        String expected = "level0 log\n" +
                "level1 log\n" +
                "level2 log\n";
        logTestHelper(expected);
    }

    @Test
    void test_level1Log() {
        Log.setLogLevel(1);
        String expected = "level0 log\n" +
                "level1 log\n";
        logTestHelper(expected);
    }

    @Test
    void test_level0Log() {
        Log.setLogLevel(0);
        String expected = "level0 log\n";
        logTestHelper(expected);
    }
}