package productsimulation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;

public class Log implements Serializable {
    private static Logger getLogger() {
        return LoggerFactory.getLogger(Log.class);
    }

    public static void setLogLevel(int level) {
        if(level >= 3) {
            Configurator.setRootLevel(Level.DEBUG);
        } else if(level == 2) {
            Configurator.setRootLevel(Level.INFO);
        } else if(level == 1) {
            Configurator.setRootLevel(Level.WARN);
        } else if(level == 0) {
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
}
