package chooseTeams;

import javax.swing.text.View;
import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    static String uploadCsvFileName ;
    static ArrayList<ArrayList<String>> finalTeams;
    public static void main(String[] args) {
        int playersCount = 0;
        int rest_thinkers = 0;
        int rest_leaders = 0;
        int rest_balancers = 0;
        double avgSkillValue = 0;
        double minAvg = 0;
        double maxAvg = 0;





        Scanner input = new Scanner(System.in);
        System.out.println("-----------Intelligent Team Formation System for University Gaming Club-----------\n");

        while (true) {
            try {
                display();
                System.out.print("Choice: ");
                int choice = getValidIntegerInput(input, 1, 9);

                switch (choice) {
                    case 1:
                        registerNewMembers(input);
                        break;
                    case 2:
                        participantSurvey(input);
                        break;
                    case 3:
                        dataFileImport(input);
                        break;
                    case 4:
                        if(uploadCsvFileName==null){
                            System.out.println("⚠️ Please upload csv file before proceeding.");
                            break;
                        }
                        playersCount = createTeams(input);

                        if (playersCount > 0) {
                            TeamMembersSelection teamMembersSelection = new TeamMembersSelection(playersCount,uploadCsvFileName);
                            finalTeams=teamMembersSelection.getFinalTeamCombination();
                            teamMembersSelection.getCreatedTeams();
                            avgSkillValue = teamMembersSelection.getAverage();
                            minAvg = teamMembersSelection.getMinimumSkillAverage();
                            maxAvg = teamMembersSelection.getMaximumSkillAverage();
                            rest_thinkers = teamMembersSelection.getRest_thinkers();
                            rest_leaders = teamMembersSelection.getRest_leaders();
                            rest_balancers = teamMembersSelection.getRest_balancers();
                        }
                        break;

                    case 5:
                        if (playersCount == 0) {
                            System.out.println("⚠️ Please create teams before proceeding.");
                            break;
                        }

                        File file = new File("formed_teams.csv");
                        if (file.exists()) {
                            ViewTeams vm = new ViewTeams(file);
                            vm.setTeamPlayerCount(playersCount);
                            vm.viewFormedTeams();
                        } else {
                            System.out.println("⚠️ Please create teams before proceeding.");
                        }
                        break;

                    case 6:
                        if (playersCount == 0) {
                            System.out.println("⚠️ Please create teams before proceeding.");
                            break;
                        }
                        System.out.printf("Average Skill Value: %.2f", avgSkillValue);
                        System.out.println();
                        System.out.printf("Team skill range %.2f - %.2f", minAvg, maxAvg);
                        System.out.println();

                        System.out.println("Remaining Leaders count is: " + rest_leaders);
                        System.out.println("Remaining Balancers count is: " + rest_balancers);
                        System.out.println("Rest thinkers count is: " + rest_thinkers);

                        ViewRemainingPlayers viewRemainingPlayers = new ViewRemainingPlayers();
                        viewRemainingPlayers.viewRemainingPlayerInCsvFile();
                        break;

                    case 7:
                        System.out.println("Exiting...");
                        input.close();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("⚠️ Invalid choice. Please try again.");
                }

            } catch (Exception e) {
                System.out.println("⚠️ An unexpected error occurred: " + e.getMessage());
                System.out.println("⚠️ Please try again.");
                input.nextLine(); // Clear the buffer
            }
        }
    }


    private static void dataFileImport(Scanner input){
        try{
            System.out.print("Enter csv file path: ");
            input.nextLine(); // Clear buffer
            String filePath = input.nextLine().trim();

            if (filePath.isEmpty()) {
                System.out.println("⚠️ File path cannot be empty. Please try again.");
                return;
            }
            HandleDataCsvFiles handleDataCsvFiles = new HandleDataCsvFiles();
            handleDataCsvFiles.createNewCsvFile(filePath);

            uploadCsvFileName= handleDataCsvFiles.getFileName();


        }catch(Exception e){
            System.out.println("⚠️ Error importing data file: " + e.getMessage());
            System.out.println("⚠️ Please try again.");
        };
    }

    private static void registerNewMembers(Scanner input){
        try{
            System.out.print("Enter your name: ");
            input.nextLine(); // Clear buffer
            String name = input.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("⚠️ Name cannot be empty. Please try again.");
                return;
            }

            System.out.print("Enter your email: ");
            String email = input.nextLine().trim();

            if (email.isEmpty() || !email.contains("@")) {
                System.out.println("⚠️ Invalid email format. Please try again.");
                return;
            }
            HandleParticipantRegistration handleParticipantRegistration = new HandleParticipantRegistration(name,email);
            if(handleParticipantRegistration.isARegisteredParticipant()){
                System.out.println("⚠️ You are already registered. Please try again.");
                return;
            }
            Player player1 = new Player(name, email);
            player1.storeRegisteredPlayerData();
            System.out.println("✅ Player successfully registered!\n");

        }catch(Exception e){
            System.out.println("⚠️ Error registering new members: " + e.getMessage());
            System.out.println("⚠️ Please try again.");
        }
    }

    private static void participantSurvey(Scanner input) {
        try {
            System.out.print("Enter your name: ");
            input.nextLine(); // Clear buffer
            String name = input.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("⚠️ Name cannot be empty. Please try again.");
                return;
            }

            System.out.print("Enter your email: ");
            String email = input.nextLine().trim();

            if (email.isEmpty() || !email.contains("@")) {
                System.out.println("⚠️ Invalid email format. Please try again.");
                return;
            }

            HandleParticipantRegistration handleParticipantRegistration = new HandleParticipantRegistration(name,email);
            if(!handleParticipantRegistration.isARegisteredParticipant()){
                System.out.println("⚠️ You are not registered. Please register first and try again.");
                return;
            }
            int rawNumber=handleParticipantRegistration.getRegisteredParticipantStoredRawNumber();


            // Personality Survey
            System.out.println("\nFollowing personality survey each statement you can rate from 1 (Strongly Disagree) to 5 (Strongly Agree)\n");

            int q1 = getValidIntegerInput(input,
                    "Q1) I enjoy taking the lead and guiding others during group activities.\nAnswer: ", 1, 5);
            int q2 = getValidIntegerInput(input,
                    "Q2) I prefer analyzing situations and coming up with strategic solutions.\nAnswer: ", 1, 5);
            int q3 = getValidIntegerInput(input,
                    "Q3) I work well with others and enjoy collaborative teamwork.\nAnswer: ", 1, 5);
            int q4 = getValidIntegerInput(input,
                    "Q4) I am calm under pressure and can help maintain team morale.\nAnswer: ", 1, 5);
            int q5 = getValidIntegerInput(input,
                    "Q5) I like making quick decisions and adapting in dynamic situations.\nAnswer: ", 1, 5);

            System.out.print("Enter your interest game(eg:- Valorant, Dota, FIFA, Basketball, Badminton): ");
            String game = input.nextLine().trim();

            if (game.isEmpty()) {
                System.out.println("⚠️ Game name cannot be empty. Please try again.");
                return;
            }

            String capitalizedGameName = game.substring(0, 1).toUpperCase() + game.substring(1);

            int gameSkillLevel = getValidIntegerInput(input,
                    "Enter your game skill level(1 to 10): ", 1, 10);

            rolesDescription();
            int roleNumber = getValidIntegerInput(input,
                    "Enter your game interesting role belongs number: ", 1, 5);

            String role = switch (roleNumber) {
                case 1 -> "Strategist";
                case 2 -> "Attacker";
                case 3 -> "Defender";
                case 4 -> "Supporter";
                case 5 -> "Coordinator";
                default -> "Unknown";
            };

            Player player1 = new Player(name, email, rawNumber);
            player1.setSkillLevel(gameSkillLevel);
            player1.setPreferredRole(role);
            player1.setInterestSport(capitalizedGameName);
            player1.calculateTotalScore(q1, q2, q3, q4, q5);

            System.out.println(player1);
            player1.storeSurveyData();


        } catch (Exception e) {
            System.out.println("⚠️ Error completing survey: " + e.getMessage());
            System.out.println("⚠️ Please try again.");
        }
    }

    private static int createTeams(Scanner input) {
        try {
            int teamPlayerCount = getValidIntegerInput(input,
                    "Enter the number of players in your team(Min players per team is 4): ", 4);

            if (teamPlayerCount < 4) {
                System.out.println("⚠️ Minimum players per team is 3. Please try again.");
                return 0;
            }

            return teamPlayerCount;

        } catch (Exception e) {
            System.out.println("⚠️ Error creating teams: " + e.getMessage());
            return 0;
        }
    }

    private static int getValidIntegerInput(Scanner input, int min, int max) {
        while (true) {
            try {
                int value = input.nextInt();

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("Please enter a number between %d and %d: ", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.print("⚠️Invalid input.\n Please enter a valid number: ");
                input.nextLine(); // Clear invalid input
            }
        }
    }
     private static int getValidIntegerInput(Scanner input, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = input.nextInt();
                input.nextLine(); // Clear the newline character
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("Please enter a number between %d and %d.\n", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.println("⚠️Invalid input!!!.\n ⚠️ Please enter a valid number.");
                input.nextLine(); // Clear invalid input
            }
        }
    }
    private static int getValidIntegerInput(Scanner input, String prompt, int min) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = input.nextInt();
                input.nextLine(); // Clear the newline character

                if (value >= min ) {
                    return value;
                } else {
                    System.out.printf("⚠️ For a team minimum players size should be %d.\n", min);
                }
            } catch (InputMismatchException e) {
                System.out.println("⚠️ Invalid input.\n⚠️ Please enter a valid number.");
                input.nextLine(); // Clear invalid input
            }
        }
    }

    public static void rolesDescription() {
        String description = """
                    Role	        |    Description
                1) Strategist	    | Focuses on tactics and planning. Keeps the bigger picture in mind during gameplay.
                2)  Attacker	    | Frontline player. Good reflexes, offensive tactics, quick execution.
                3)  Defender	    | Protects and supports team stability. Good under pressure and team-focused.
                4)  Supporter	    | Jack-of-all-trades. Adapts roles, ensures smooth coordination.
                5)  Coordinator	    | Communication lead. Keeps the team informed and organized in real time.
                
                """;
        System.out.println(description);
    }

    public static void display() {
        String menu = """
                \n
                1) Club Registration\s
                2) Participant Survey
                3) Import Participant Data
                4) Generate Teams
                5) Team Overview
                6) Team formation details
                7) Exit
               \s
               \s""";
        System.out.println(menu);
    }
}