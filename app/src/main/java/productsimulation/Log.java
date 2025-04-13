package productsimulation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Log implements Serializable {
    private static Logger getLogger() {
        return LoggerFactory.getLogger(Log.class);
    }

    public static void setLogLevel(int level) {
        if(level >= 3) {
            level0Log("verbosity change to: debug");
            Configurator.setRootLevel(Level.DEBUG);
        } else if(level == 2) {
            level0Log("verbosity change to: 2");
            Configurator.setRootLevel(Level.INFO);
        } else if(level == 1) {
            level0Log("verbosity change to: 1");
            Configurator.setRootLevel(Level.WARN);
        } else if(level == 0) {
            level0Log("verbosity change to: 1");
            Configurator.setRootLevel(Level.ERROR);
        }
    }

    // the most detailed
    public static void debugLog(String logBody) {
        getLogger().debug(logBody);
    }

    public static void level2Log(String logBody) {
        getLogger().info(logBody);
    }

    public static void level1Log(String logBody) {
        getLogger().warn(logBody);
    }

    // the least detailed
    public static void level0Log(String logBody) {
        getLogger().error(logBody);
    }

//    GUI会每隔一秒调用一次，返回全量Log，GUI默认将滚动条拉到最下
    public static String getLogText() {
        StringBuilder logContent = new StringBuilder();
        File logFile = new File("test.log");
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }
        } catch (IOException e) {
            return "error when reading log file: " + e.getMessage();
        }
        return logContent.toString().trim();
//        return "test log string 0412";
    }
}
