package main;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.*;


public class Login {
    private String currentUserName;
    private String currentUserEmail;
    private int currentUserStoredRawNumber;
    private static final Logger logger = Logger.getLogger(TeamFormationTask.class.getName());

    public String getCurrentUserName() {
        return currentUserName;
    }
    public String getCurrentUserEmail() {
        return currentUserEmail;
    }
    public int getCurrentUserStoredRawNumber() {
        return currentUserStoredRawNumber;
    }


    public boolean participantLogin(Scanner input) {
        setupLogger();//2.3
        logger.info("Starting login for participant");
        InputValidator inputValidator = new InputValidator();//2.4
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("                         PARTICIPANT LOGIN");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            currentUserName = inputValidator.isValidStringInput(input,
                    "Enter the name (or 'q' to return): ");//2.5

            if (currentUserName.equalsIgnoreCase("q")) {
                System.out.println("🔙 Returning to main menu...");
                return false;
            }


            if (currentUserName.isEmpty()) {
                System.out.println("⚠️ Name cannot be empty.Please try again.");
                logger.warning("⚠️ Name cannot be empty.");
                return false;
            }


            currentUserEmail = inputValidator.isValidStringInput(input,
                    "Enter the email (or 'q' to return): ");//3.3

            if (currentUserEmail.equalsIgnoreCase("q")) {
                System.out.println("🔙 Returning to main menu...");
                return false;
            }

            if (currentUserEmail.isEmpty() || !currentUserEmail.contains("@")) {
                System.out.println("⚠️ Invalid email format.");
                logger.warning("⚠️ Invalid email format for " + currentUserName);
                return false;
            }


            HandleParticipantRegistration handleParticipantRegistration =
                    new HandleParticipantRegistration(currentUserName, currentUserEmail);//5
//5.1
            if (!handleParticipantRegistration.isARegisteredParticipant()) {
                System.out.println("❌ You are not registered. Please register first.");
                logger.warning(currentUserName + " is not registered. Please register first.");
                return false;
            }

            currentUserStoredRawNumber = handleParticipantRegistration.getRegisteredParticipantStoredRawNumber();
            display("✅ Login successful! Welcome, " + currentUserName + ".");
            logger.info("Login successful for " + currentUserName);
            return true;

        } catch (Exception e) {
            System.out.println("⚠️ Error during login: " + e.getMessage());
            return false;
        }


    }
    public void display(String message){
        System.out.println(message);
    }

    public  boolean organizerLogin(Scanner input) {
        setupLogger();//2.3
        logger.info("Starting login for organizer");
        InputValidator inputValidator = new InputValidator();//2.4
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("                         ORGANIZER LOGIN");
            System.out.println("-".repeat(80));
            System.out.println("\n");


            String organizerName = inputValidator.isValidStringInput(input,
                    "Enter Organizer name (or 'q' to return): ");//2.5

            if (organizerName == null) {
                System.out.println("🔙 Returning to main menu...");
                return false;
            }


            String organizerEmail = inputValidator.isValidStringInput(input,
                    "Enter Organizer Email (or 'q' to return): ");//3.3

            if (organizerEmail == null) {
                System.out.println("🔙 Returning to main menu...");
                return false;
            }

            if (!organizerEmail.contains("@")) {
                System.out.println("⚠️ Invalid email format.");
                logger.warning("⚠️ Invalid email format.");
                return false;
            }

            HandleOrganizerRegistration handleOrganizerRegistration =
                    new HandleOrganizerRegistration(organizerName, organizerEmail);//5
            //5.1
            if (!handleOrganizerRegistration.isARegisteredOrganizer()) {
                System.out.println("❌ Invalid Organizer credentials. Please try again.");
                logger.warning("Invalid Organizer credentials for " + organizerName);
                return false;
            }
            System.out.println("✅ Login successful! Welcome, Organizer.");
            logger.info("Login successful for Organizer " + organizerName);
            return true;

        } catch (Exception e) {
            System.out.println("⚠️ Error during login: " + e.getMessage());
            return false;
        }
    }
    public static void setupLogger() {
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
            FileHandler fileHandler = new FileHandler("system.log",true); // true = append mode
            fileHandler.setFormatter(new SimpleFormatter());

            // Add file handler to root logger
            rootLogger.addHandler(fileHandler);

            // Set log level
            rootLogger.setLevel(Level.INFO);

        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
        }
    }


}
