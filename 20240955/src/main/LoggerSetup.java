package main;

import java.io.IOException;
import java.util.logging.*;

public class LoggerSetup {
    private static boolean isInitialized = false;

    public static void initialize() {
        if (isInitialized) {
            return; // Already set up, skip
        }

        try {
            // Remove default console handlers
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                if (handler instanceof ConsoleHandler) {
                    rootLogger.removeHandler(handler);
                }
            }

            // Create file handler
            FileHandler fileHandler = new FileHandler("system.log", true); // append mode
            fileHandler.setFormatter(new SimpleFormatter());

            // Add file handler to root logger
            rootLogger.addHandler(fileHandler);

            // Set log level
            rootLogger.setLevel(Level.INFO);

            isInitialized = true;

            // Log initialization
            Logger.getLogger(LoggerSetup.class.getName()).info("System started. Logging initialized.");

        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
        }
    }
}