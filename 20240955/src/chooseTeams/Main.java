package chooseTeams;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static String uploadCsvFileName;
    static String currentUserName;
    static String currentUserEmail;
    static int currentUserStoredRawNumber;


    static int totalFormedTeams = 0;
    static int playersCount = 0;
    static int rest_thinkers = 0;
    static int rest_leaders = 0;
    static int rest_balancers = 0;
    static double avgSkillValue = 0;
    static double minAvg = 0;
    static double maxAvg = 0;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("=".repeat(80));
        System.out.println("\n      Intelligent Team Formation System for University Gaming Club\n");
        System.out.println("=".repeat(80));
        System.out.println("\n");

        mainMenu(input);
        input.close();
    }


    private static void mainMenu(Scanner input) {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("                              MAIN MENU");
                System.out.println("=".repeat(80));
                System.out.println("\n 1) Login as Organizer");
                System.out.println(" 2) Login as Participant");
                System.out.println(" 3) Register New Participant");
                System.out.println(" 4) Register New Organizer");
                System.out.println(" 5) Exit\n");
                System.out.println("=".repeat(80));
                System.out.println("\n");

                int choice = getValidIntegerInput(input, "Enter your choice: ", 1, 5);

                switch (choice) {
                    case 1:
                        if (organizerLogin(input)) {
                            organizerDashboard(input);
                        }
                        break;
                    case 2:
                        if (participantLogin(input)) {
                            participantDashboard(input);
                        }
                        break;
                    case 3:
                        registerNewMembers(input);
                        break;
                    case 4:
                        registerNewOrganizer(input);
                        break;
                    case 5:
                        System.out.println("\n✅ Thank you for using the Team Formation System. Goodbye!");
                        return;
                }
            } catch (Exception e) {
                System.out.println("⚠️ An unexpected error occurred: " + e.getMessage());
                System.out.println("⚠️ Returning to main menu...");
                input.nextLine();
            }
        }
    }


    private static boolean organizerLogin(Scanner input) {
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

    private static void organizerDashboard(Scanner input) {
        while (true) {
            try {
                System.out.println("\n\n" + "=".repeat(80));
                System.out.println("                          ORGANIZER DASHBOARD");
                System.out.println("=".repeat(80));
                System.out.println("\n 1) Import Participant Data (CSV)");
                System.out.println(" 2) Generate Teams");
                System.out.println(" 3) Random final teams selection");
                System.out.println(" 4) Review Randomly Selected Teams");// add this in use case
                System.out.println(" 5) View Team Statistics");
                System.out.println(" 6) View Remaining Players");
                System.out.println(" 7) Review Generated all Teams");//
                System.out.println(" 8) Logout\n");
                System.out.println("=".repeat(80));
                System.out.println("\n");

                int choice = getValidIntegerInput(input, "Enter your choice: ", 1, 8);

                switch (choice) {
                    case 1:
                        dataFileImport(input);
                        break;
                    case 2:
                        generateTeams(input);
                        break;
                    case 3:
                        finalTeamsSelection(input);
                        break;
                    case 4:
                        reviewRandomlySelectedTeams(input);
                        break;
                    case 5:
                        viewTeamStatistics();
                        break;
                    case 6:
                        viewRemainingPlayers();
                        break;
                    case 7:
                        reviewGeneratedTeams();
                        break;
                    case 8:
                        System.out.println("🔓 Logging out...");
                        return;
                }
            } catch (Exception e) {
                System.out.println("⚠️ An error occurred: " + e.getMessage());
                input.nextLine(); // Clear buffer
            }
        }
    }


    private static boolean participantLogin(Scanner input) {
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


            currentUserEmail =  getStringInputWithExit(input,
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
            return true;

        } catch (Exception e) {
            System.out.println("⚠️ Error during login: " + e.getMessage());
            return false;
        }
    }


    private static void participantDashboard(Scanner input) {
        while (true) {
            try {
                System.out.println("\n\n" + "=".repeat(80));
                System.out.println("\n                          PARTICIPANT DASHBOARD\n");
                System.out.println("=".repeat(80));
                System.out.println("\n Welcome, " + currentUserName + "!");
                System.out.println("-".repeat(80));
                System.out.println("\n 1) Complete Personality Survey");
                System.out.println(" 2) View My Profile");
                System.out.println(" 3) Review assigned team");
                System.out.println(" 4) Logout\n");
                System.out.println("=".repeat(80));
                System.out.println("\n");

                int choice = getValidIntegerInput(input, "Enter your choice: ", 1, 4);

                switch (choice) {
                    case 1:
                        participantSurvey(input);
                        break;
                    case 2:
                        System.out.println("\n📋 Your Profile:");
                        System.out.println("   Name: " + currentUserName);
                        System.out.println("   Email: " + currentUserEmail);
                        break;

                    case 3:
                        reviewParticipantAssignedTeam();
                        break;
                    case 4:
                        System.out.println("🔓 Logging out...");
                        currentUserName = null;
                        currentUserEmail = null;
                        currentUserStoredRawNumber = 0;
                        return;
                }
            } catch (Exception e) {
                System.out.println("⚠️ An error occurred: " + e.getMessage());
                input.nextLine();
            }
        }
    }


    private static void registerNewMembers(Scanner input) {
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
                return;
            }

            Participant player1 = new Participant(name, email);
            player1.storeRegisteredPlayerData();
            System.out.println("✅ Registration successful! You can now login as a participant.\n");

        } catch (Exception e) {
            System.out.println("⚠️ Error during registration: " + e.getMessage());
        }
    }

    private static void registerNewOrganizer(Scanner input) {
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

            if (handleOrganizerRegistration.isARegisteredParticipant()) {
                System.out.println("⚠️ This email is already registered.");
                return;
            }

            Organizer organizer = new Organizer(name, email);
            organizer.storeRegisteredOrganizerData();
            System.out.println("✅ Registration successful! You can now login as a organizer.\n");

        } catch (Exception e) {
            System.out.println("⚠️ Error during registration: " + e.getMessage());
        }
    }


    private static void participantSurvey(Scanner input) {
        try {
            System.out.println("\n\n" + "=".repeat(80));
            System.out.println("                          PERSONALITY SURVEY");
            System.out.println("=".repeat(80));
            System.out.println("\n");
            System.out.println("Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)\n");

            int q1 = getValidIntegerInput(input,
                    "Q1) I enjoy taking the lead and guiding others during group activities.\n    Answer: ", 1, 5);
            int q2 = getValidIntegerInput(input,
                    "Q2) I prefer analyzing situations and coming up with strategic solutions.\n    Answer: ", 1, 5);
            int q3 = getValidIntegerInput(input,
                    "Q3) I work well with others and enjoy collaborative teamwork.\n    Answer: ", 1, 5);
            int q4 = getValidIntegerInput(input,
                    "Q4) I am calm under pressure and can help maintain team morale.\n    Answer: ", 1, 5);
            int q5 = getValidIntegerInput(input,
                    "Q5) I like making quick decisions and adapting in dynamic situations.\n    Answer: ", 1, 5);


            String game = getStringInputWithExit(input,
                    "\nEnter your game of interest (e.g., Valorant, Dota, FIFA, Basketball) or 'q' to cancel: ");

            if (game == null) {
                System.out.println("🔙 Survey cancelled.");
                return;
            }

            String capitalizedGameName = game.substring(0, 1).toUpperCase() + game.substring(1);

            int gameSkillLevel = getValidIntegerInput(input,
                    "Enter your skill level (1-10): ", 1, 10);

            rolesDescription();
            int roleNumber = getValidIntegerInput(input,
                    "Enter the number of your preferred role: ", 1, 5);

            String role = switch (roleNumber) {
                case 1 -> "Strategist";
                case 2 -> "Attacker";
                case 3 -> "Defender";
                case 4 -> "Supporter";
                case 5 -> "Coordinator";
                default -> "Unknown";
            };

            Participant player1 = new Participant(currentUserName, currentUserEmail);
            player1.setSkillLevel(gameSkillLevel);
            player1.setPreferredRole(role);
            player1.setInterestSport(capitalizedGameName);
            player1.calculateTotalScore(q1, q2, q3, q4, q5);
            boolean isSurveyValid = player1.checkPersonalityType();
            player1.storeSurveyData();

            if (isSurveyValid) {
                System.out.println("\n✅ Survey completed successfully!");
                System.out.println("📊 Your personality type has been determined based on your responses.");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Error completing survey: " + e.getMessage());
        }
    }


    private static void dataFileImport(Scanner input) {
        try {
            System.out.println("\n\n" + "-".repeat(80));
            System.out.println("\n                      IMPORT PARTICIPANT DATA\n");
            System.out.println("-".repeat(80));
            System.out.println("\n");

            // Updated with 'q' exit functionality
            String filePath = getStringInputWithExit(input,
                    "Enter CSV file path (or 'q' to cancel): ");

            if (filePath == null) {
                System.out.println("🔙 Import cancelled.");
                return;
            }

            HandleDataCsvFiles handleDataCsvFiles = new HandleDataCsvFiles();
            handleDataCsvFiles.createNewCsvFile(filePath);
            uploadCsvFileName = handleDataCsvFiles.getFileName();

            System.out.println("✅ Data imported successfully from: " + uploadCsvFileName);

        } catch (Exception e) {
            System.out.println("⚠️ Error importing data: " + e.getMessage());
        }
    }

    private static void generateTeams(Scanner input) {
        if (uploadCsvFileName == null) {
            System.out.println("⚠️ Please import participant data first.");
            return;
        }

        try {
            playersCount = getValidIntegerInput(input,
                    "\nEnter players per team (minimum 4): ", 4);

            System.out.println("\n🔄 Starting team formation with parallel processing...");

            long startTime = System.currentTimeMillis();
            TeamMembersSelection teamMembersSelection = new TeamMembersSelection(playersCount, uploadCsvFileName);
            teamMembersSelection.categorizeByPersonalityType();
            teamMembersSelection.createTeams();
//            teamMembersSelection.randomizeTeamCombinationAgain();
            long endTime = System.currentTimeMillis();

            avgSkillValue = teamMembersSelection.getAverage();
            minAvg = teamMembersSelection.getMinimumSkillAverage();
            maxAvg = teamMembersSelection.getMaximumSkillAverage();
            rest_thinkers = teamMembersSelection.getRest_thinkers();
            rest_leaders = teamMembersSelection.getRest_leaders();
            rest_balancers = teamMembersSelection.getRest_balancers();
            totalFormedTeams = teamMembersSelection.getFinalTeamCombination();
            teamMembersSelection.writeFinalTeamsOnCsvFile();

            File file = new File("files/possible_teams.csv");
            if (file.exists()) {
                ViewTeams vm = new ViewTeams(file);
                vm.setTeamPlayerCount(playersCount);
                vm.viewFormedTeams();
            } else {
                System.out.println("⚠️ No teams file found. Please generate teams first.");
            }

            boolean isAcceptingFormedTeams=getValidResponseInput(input,"\nDo you accept these teams? (Y/N):","y","n");
            if(isAcceptingFormedTeams){
                teamMembersSelection.exportFormedTeams("files/possible_teams.csv");
                teamMembersSelection.writeRemainingPlayerInCsvFile();

            }else{
                playersCount=0;
                System.out.println("❌ Teams were not accepted. Export cancelled.");
                File file1 = new File("possible_teams.csv");
                if (file1.exists()) {
                    HandleDataCsvFiles handleDataCsvFiles = new HandleDataCsvFiles();
                    handleDataCsvFiles.deleteCsvFile(file1);
                }

            }

            System.out.println("\n⏱️ Team formation completed in " + (endTime - startTime) + "ms");

        } catch (Exception e) {
            System.out.println("⚠️ Error generating teams: " + e.getMessage());
        }
    }

    private static void reviewRandomlySelectedTeams(Scanner input) {
        if (playersCount == 0) {
            System.out.println("⚠️ Please generate teams first.");
            return;
        }

        File file = new File("formed_teams.csv");
        if (file.exists()) {
            FinalTeamSelection finalTeamSelection=new FinalTeamSelection();
            finalTeamSelection.viewFormedTeams(file);
        } else {
            System.out.println("⚠️ Please generate final teams first.");
        }


        boolean isAcceptingFormedTeams=getValidResponseInput(input,"\nDo you accept these teams? (Y/N):","y","n");
        if(isAcceptingFormedTeams){
            System.out.println("✅ Final teams accepted. Exporting...");
            try {
                // Sleep for 2 seconds (2000 ms)
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("✅ Final teams exported successfully to: formed_teams.csv");

        }else {
            playersCount = 0;
            System.out.println("❌ Teams were not accepted. Export cancelled.");
            File file1 = new File("formed_teams.csv");
            if (file1.exists()) {
                HandleDataCsvFiles handleDataCsvFiles = new HandleDataCsvFiles();
                handleDataCsvFiles.deleteCsvFile(file1);
            }
        }


    }

    private static void reviewGeneratedTeams() {
        if (playersCount == 0) {
            System.out.println("⚠️ Please generate teams first.");
            return;
        }
        File file_check = new File("files/possible_teams.csv");
        File file = new File("possible_teams.csv");
        if (file_check.exists()) {
            ViewTeams vm = new ViewTeams(file);
            vm.setTeamPlayerCount(playersCount);
            vm.viewFormedTeams();
        } else {
            System.out.println("⚠️ No teams found. Please generate teams first.");
        }
    }
    private static void reviewParticipantAssignedTeam() {


        File file = new File("formed_teams.csv");
        if (file.exists()) {
            ViewRemainingPlayers viewRemainingPlayers = new ViewRemainingPlayers();
            viewRemainingPlayers.viewPlayersInCsvFile(file);
        } else {
            System.out.println("⚠️ No teams found. Please generate teams first.");
        }
    }

    private static void viewTeamStatistics(   ) {
        if (playersCount == 0) {
            System.out.println("⚠️ Please generate teams first.");
            return;
        }
        File file = new File("files/possible_teams.csv");
        if (!file.exists()) {
            System.out.println("⚠️ No teams found. Please generate teams first.");
        }

        System.out.println("\n\n" + "=".repeat(80));
        System.out.println("\n                          TEAM STATISTICS");
        System.out.println("=".repeat(80));
        System.out.println("\n📊 Total teams formed: " + totalFormedTeams);
        System.out.printf("Average Skill Value: %.2f\n", avgSkillValue);
        System.out.printf("Formed Teams Skill Range: %.2f - %.2f\n", minAvg, maxAvg);
        System.out.println("-".repeat(80));
        System.out.println("Remaining Players by Type:");
        System.out.println("  Leaders: " + rest_leaders);
        System.out.println("  Balancers: " + rest_balancers);
        System.out.println("  Thinkers: " + rest_thinkers);
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("\n");
    }

    private static void finalTeamsSelection(Scanner input){

        File file_check = new File("formed_teams.csv");
        if (!file_check.exists()) {
            System.out.println("⚠️ No teams found. Please generate teams first.");
        }

        if (playersCount == 0) {
            System.out.println("⚠️ Please generate teams first.");
            return;
        }
        System.out.println("\n📊 Total teams formed: " + totalFormedTeams);
        File file = new File("files/possible_teams.csv");

        try{
            int requiredTeamCount = getValidIntegerInput(input,
                    "\nEnter required teams count (minimum 1): ", 1, totalFormedTeams);

            FinalTeamSelection finalTeamSelection=new FinalTeamSelection();
            finalTeamSelection.setRequiredTeamCount(requiredTeamCount);
            finalTeamSelection.setTotalFormedTeamsCount(totalFormedTeams);
            finalTeamSelection.setCsvFile(file);
            finalTeamSelection.finalResult();
        }catch (Exception e){
            System.out.println(e.getMessage());

        }
        }

    private static void viewRemainingPlayers() {
        if (playersCount == 0) {
            System.out.println("⚠️ Please generate teams first.");
            return;
        }

        ViewRemainingPlayers viewRemainingPlayers = new ViewRemainingPlayers();
        viewRemainingPlayers.viewPlayersInCsvFile();
    }


    private static boolean getValidResponseInput(Scanner input, String prompt, String y, String n) {
        while (true) {
            try {
                System.out.print(prompt);
                String value = input.nextLine().trim().toLowerCase();
                if (value.equals(y) || value.equals(n)) {
                    if(value.equals("y")){
                        return true;
                    }else{
                        return false;
                    }
                }else {
                    System.out.printf("Please enter a valid response: %s or %s.\n ", y, n);
                }
            } catch (InputMismatchException e) {
                System.out.print("⚠️ Invalid input. Please enter a valid number: ");
                input.nextLine();
            }
        }
    }

    private static int getValidIntegerInput(Scanner input, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = input.nextInt();
                input.nextLine();
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("Please enter a number between %d and %d.\n", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.println("⚠️ Invalid input. Please enter a valid number.");
                input.nextLine();
            }
        }
    }

    private static int getValidIntegerInput(Scanner input, String prompt, int min) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = input.nextInt();
                input.nextLine();
                if (value >= min) {
                    return value;
                } else {
                    System.out.printf("⚠️ Minimum value should be %d.\n", min);
                }
            } catch (InputMismatchException e) {
                System.out.println("⚠️ Invalid input. Please enter a valid number.");
                input.nextLine(); // Clear invalid input
            }
        }
    }
    private static String getStringInputWithExit(Scanner input, String prompt) {
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
    private static void rolesDescription() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                              GAME ROLES");
        System.out.println("=".repeat(80));
        System.out.println("\nRole          | Description");
        System.out.println("-".repeat(80));
        System.out.println("1) Strategist | Focuses on tactics and planning. Keeps the bigger picture in mind.");
        System.out.println("2) Attacker   | Frontline player. Good reflexes, offensive tactics, quick execution.");
        System.out.println("3) Defender   | Protects and supports team stability. Good under pressure.");
        System.out.println("4) Supporter  | Jack-of-all-trades. Adapts roles, ensures smooth coordination.");
        System.out.println("5) Coordinator| Communication lead. Keeps the team informed and organized.\n");
        System.out.println("=".repeat(80) + "\n");
    }
}