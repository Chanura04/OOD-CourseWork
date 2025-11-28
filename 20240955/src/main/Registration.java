package main;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.*;

public class Registration {
    private static final Logger logger = Logger.getLogger(TeamFormationTask.class.getName());


    public void registerNewParticipant(Scanner input) {
        setupLogger();//2.3
        logger.info("Starting registration for new participant");
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("                      NEW PARTICIPANT REGISTRATION");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            System.out.print("Enter your name (or 'q' to cancel): ");//2.4
            String name = input.nextLine().trim();

            if (name.equalsIgnoreCase("q")) {
                System.out.println("🔙 Registration cancelled.");//3.1
                return;//3.2
            }

            if (name.isEmpty()) {
                System.out.println("⚠️ Name cannot be empty.");//3.3
                return;//3.4
            }

            System.out.print("Enter your email: ");//4
            String email = input.nextLine().trim();

            if (email.equalsIgnoreCase("q")) {//4.1
                System.out.println("🔙 Registration cancelled.");
                return;//4.2
            }

            if (email.isEmpty() || !email.contains("@")) {//4.3
                System.out.println("⚠️ Invalid email format.");
                return;//4.4
            }

            HandleParticipantRegistration handleParticipantRegistration =
                    new HandleParticipantRegistration(name, email);//4.5

            if (handleParticipantRegistration.isARegisteredParticipant()) {//4.6
                System.out.println("⚠️ This email is already registered.");
                logger.info(email + " is already registered.");
                return;
            }

            Participant player = new Participant(name, email);//4.7
            player.storeRegisteredPlayerData();
            System.out.println("✅ Registration successful! You can now login as a participant.\n");
            logger.info("✅ New participant registered: " + name);
        } catch (Exception e) {
            System.out.println("⚠️ Error during registration: " + e.getMessage());
        }
    }

    public void registerNewOrganizer(Scanner input) {
        setupLogger();//2.3
        logger.info("Starting registration for new organizer");
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("                      NEW ORGANIZER REGISTRATION");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            System.out.print("Enter your name (or 'q' to cancel): ");//2.4
            String name = input.nextLine().trim();

            if (name.equalsIgnoreCase("q")) {
                System.out.println("🔙 Registration cancelled.");//3.1
                return;//3.2
            }

            if (name.isEmpty()) {//3.3
                System.out.println("⚠️ Name cannot be empty.");
                return;//3.4
            }

            System.out.print("Enter your email: ");//3.5
            String email = input.nextLine().trim();

            if (email.equalsIgnoreCase("q")) {//4.1
                System.out.println("🔙 Registration cancelled.");
                return;//4.2
            }

            if (email.isEmpty() || !email.contains("@")) {//4.3
                System.out.println("⚠️ Invalid email format.");
                return;//4.4
            }

            HandleOrganizerRegistration handleOrganizerRegistration =
                    new HandleOrganizerRegistration(name, email);//4.5

            if (handleOrganizerRegistration.isARegisteredOrganizer()) {//4.6
                System.out.println("⚠️ This email is already registered.");
                logger.info(email + " is already registered.");
                return;//4.6.2
            }

            Organizer organizer = new Organizer(name, email);//4.7
            organizer.storeRegisteredOrganizerData();//4.8
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
