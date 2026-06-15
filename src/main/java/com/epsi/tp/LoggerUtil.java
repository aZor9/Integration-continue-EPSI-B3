package com.epsi.tp;

import java.util.logging.Logger;
import java.util.logging.Level;

public class LoggerUtil {
    private static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    private LoggerUtil() {
        // Constructeur private
    }

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void severe(String message) {
        logger.log(Level.SEVERE, message);
    }

    public static void error(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
