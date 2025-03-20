package productsimulation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

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
        logger.debug(logBody);
    }

    public static void level2Log(String logBody) {
        logger.info(logBody);
    }

    public static void level1Log(String logBody) {
        logger.warn(logBody);
    }

    // the least detailed
    public static void level0Log(String logBody) {
        logger.error(logBody);
    }
}
