package main;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.*;

public class Registration {
    private static final Logger logger = Logger.getLogger(TeamFormationTask.class.getName());


    public void registerNewParticipant(Scanner input) {
        setupLogger();
        logger.info("Starting registration for new participant");
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("                      NEW PARTICIPANT REGISTRATION");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            System.out.print("Enter your name (or 'q' to cancel): ");
            String name = input.nextLine().trim();

            if (name.equalsIgnoreCase("q")) {
                System.out.println("🔙 Registration cancelled.");
                return;
            }

            if (name.isEmpty()) {
                System.out.println("⚠️ Name cannot be empty.");
                return;
            }

            System.out.print("Enter your email: ");
            String email = input.nextLine().trim();

            if (email.equalsIgnoreCase("q")) {
                System.out.println("🔙 Registration cancelled.");
                return;
            }

            if (email.isEmpty() || !email.contains("@")) {
                System.out.println("⚠️ Invalid email format.");
                return;
            }

            HandleParticipantRegistration handleParticipantRegistration =
                    new HandleParticipantRegistration(name, email);

            if (handleParticipantRegistration.isARegisteredParticipant()) {
                System.out.println("⚠️ This email is already registered.");
                logger.info(email + " is already registered.");
                return;
            }

            Participant player1 = new Participant(name, email);
            player1.storeRegisteredPlayerData();
            System.out.println("✅ Registration successful! You can now login as a participant.\n");
            logger.info("✅ New participant registered: " + name);
        } catch (Exception e) {
            System.out.println("⚠️ Error during registration: " + e.getMessage());
        }
    }

    public void registerNewOrganizer(Scanner input) {
        setupLogger();
        logger.info("Starting registration for new organizer");
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("                      NEW ORGANIZER REGISTRATION");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            System.out.print("Enter your name (or 'q' to cancel): ");
            String name = input.nextLine().trim();

            if (name.equalsIgnoreCase("q")) {
                System.out.println("🔙 Registration cancelled.");
                return;
            }

            if (name.isEmpty()) {
                System.out.println("⚠️ Name cannot be empty.");
                return;
            }

            System.out.print("Enter your email: ");
            String email = input.nextLine().trim();

            if (email.equalsIgnoreCase("q")) {
                System.out.println("🔙 Registration cancelled.");
                return;
            }

            if (email.isEmpty() || !email.contains("@")) {
                System.out.println("⚠️ Invalid email format.");
                return;
            }

            HandleOrganizerRegistration handleOrganizerRegistration =
                    new HandleOrganizerRegistration(name, email);

            if (handleOrganizerRegistration.isARegisteredOrganizer()) {
                System.out.println("⚠️ This email is already registered.");
                logger.info(email + " is already registered.");
                return;
            }

            Organizer organizer = new Organizer(name, email);
            organizer.storeRegisteredOrganizerData();
            System.out.println("✅ Registration successful! You can now login as a organizer.\n");
            logger.info("✅ Registration successful! You can now login as a organizer.");
        } catch (Exception e) {
            System.out.println("⚠️ Error during registration: " + e.getMessage());
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
            FileHandler fileHandler = new FileHandler("team_formation.log",true); // true = append mode
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
