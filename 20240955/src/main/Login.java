package main;

import java.util.Scanner;


public class Login {
    private String currentUserName;
    private String currentUserEmail;
    private int currentUserStoredRawNumber;

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
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("                         PARTICIPANT LOGIN");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            currentUserName = getStringInputWithExit(input,
                    "Enter the name (or 'q' to return): ");

            if (currentUserName.equalsIgnoreCase("q")) {
                System.out.println("🔙 Returning to main menu...");
                return false;
            }


            if (currentUserName.isEmpty()) {
                System.out.println("⚠️ Name cannot be empty.");
                return false;
            }


            currentUserEmail = getStringInputWithExit(input,
                    "Enter the email (or 'q' to return): ");

            if (currentUserEmail.equalsIgnoreCase("q")) {
                System.out.println("🔙 Returning to main menu...");
                return false;
            }

            if (currentUserEmail.isEmpty() || !currentUserEmail.contains("@")) {
                System.out.println("⚠️ Invalid email format.");
                return false;
            }


            HandleParticipantRegistration handleParticipantRegistration =
                    new HandleParticipantRegistration(currentUserName, currentUserEmail);

            if (!handleParticipantRegistration.isARegisteredParticipant()) {
                System.out.println("❌ You are not registered. Please register first.");
                return false;
            }

            currentUserStoredRawNumber = handleParticipantRegistration.getRegisteredParticipantStoredRawNumber();
            System.out.println("✅ Login successful! Welcome, " + currentUserName + ".");

//            Dashboard dashboard = new Dashboard();
//            dashboard.setCurrentUserName(currentUserName);
//            dashboard.setCurrentUserEmail(currentUserEmail);
//            dashboard.setCurrentUserStoredRawNumber(currentUserStoredRawNumber);

            return true;

        } catch (Exception e) {
            System.out.println("⚠️ Error during login: " + e.getMessage());
            return false;
        }


    }


    public  boolean organizerLogin(Scanner input) {
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("                         ORGANIZER LOGIN");
            System.out.println("-".repeat(80));
            System.out.println("\n");


            String organizerName = getStringInputWithExit(input,
                    "Enter Organizer name (or 'q' to return): ");

            if (organizerName == null) {
                System.out.println("🔙 Returning to main menu...");
                return false;
            }


            String organizerEmail = getStringInputWithExit(input,
                    "Enter Organizer Email (or 'q' to return): ");

            if (organizerEmail == null) {
                System.out.println("🔙 Returning to main menu...");
                return false;
            }

            if (!organizerEmail.contains("@")) {
                System.out.println("⚠️ Invalid email format.");
                return false;
            }

            HandleOrganizerRegistration handleOrganizerRegistration =
                    new HandleOrganizerRegistration(organizerName, organizerEmail);

            if (!handleOrganizerRegistration.isARegisteredParticipant()) {
                System.out.println("❌ Invalid Organizer credentials. Please try again.");
                return false;
            }
            System.out.println("✅ Login successful! Welcome, Organizer.");
            return true;

        } catch (Exception e) {
            System.out.println("⚠️ Error during login: " + e.getMessage());
            return false;
        }
    }

    public  String getStringInputWithExit(Scanner input, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String value = input.nextLine().trim();

                if (value.equalsIgnoreCase("q")) {
                    return null; // Signal to exit
                }

                if (!value.isEmpty()) {
                    return value;
                }

                System.out.println("⚠️ Input cannot be empty. Please try again or enter 'q' to cancel.");

            } catch (Exception e) {
                System.out.println("⚠️ Error reading input: " + e.getMessage());
                input.nextLine(); // Clear buffer
            }
        }
    }

}
