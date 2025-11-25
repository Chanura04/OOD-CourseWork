package main;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Dashboard {
    private int totalFormedTeams = 0;
    private int playersCountPerTeam = 0;
    private int remainingThinkersCount = 0;
    private int remainingLeadersCount = 0;
    private int remainingBalancersCount = 0;
    private double avgSkillValue = 0;
    private double minAvg = 0;
    private double maxAvg = 0;
    private String uploadCsvFileName;

    private String currentUserName;
    private String currentUserEmail;
    private int currentUserStoredRawNumber;

    public void setCurrentUserName(String currentUserName){
        this.currentUserName=currentUserName;
    }
    public void setCurrentUserEmail(String currentUserEmail){
        this.currentUserEmail=currentUserEmail;
    }
    public void setCurrentUserStoredRawNumber(int currentUserStoredRawNumber){
        this.currentUserStoredRawNumber=currentUserStoredRawNumber;
    }





    public  void organizerDashboard(Scanner input) {
        while (true) {
            try {
                System.out.println("\n\n" + "=".repeat(80));
                System.out.println("                          ORGANIZER DASHBOARD");
                System.out.println("=".repeat(80));
                System.out.println("\n 1) Import Participant Data (CSV)");
                System.out.println(" 2) Generate Teams");
                System.out.println(" 3) Random final teams selection");
                System.out.println(" 4) Review Randomly Selected Teams");
                System.out.println(" 5) View Team Statistics");
                System.out.println(" 6) View Remaining Players");
                System.out.println(" 7) Review Generated all Teams");
                System.out.println(" 8) Logout\n");
                System.out.println("=".repeat(80));
                System.out.println("\n");

                int choice = getValidIntegerInput(input, "Enter your choice: ", 1, 8);

                switch (choice) {
                    case 1:
                        HandleUploadedDataCsvFiles handleDataCsvFiles = new HandleUploadedDataCsvFiles();
                        handleDataCsvFiles.dataFileImport(input);
                        uploadCsvFileName = handleDataCsvFiles.getUploadCsvFileName();
                        break;
                    case 2:
                        TeamMembersSelection teamMembersSelection = new TeamMembersSelection(uploadCsvFileName);
                        teamMembersSelection.defineTeamSize(input);
                        teamMembersSelection.generateTeams(input);
                        playersCountPerTeam=teamMembersSelection.getTeamPlayerCount();
                        totalFormedTeams=teamMembersSelection.getTotalFinalTeamCombination();
                        avgSkillValue = teamMembersSelection.getAverage();
                        minAvg = teamMembersSelection.getMinimumSkillAverage();
                        maxAvg = teamMembersSelection.getMaximumSkillAverage();
                        remainingBalancersCount=teamMembersSelection.getRemainingBalancersCount();
                        remainingLeadersCount=teamMembersSelection.getRemainingLeadersCount();
                        remainingThinkersCount=teamMembersSelection.getRemainingThinkersCount();
                        break;
                    case 3:
                        File fileForFinalTeamSelection=new File("files/possible_teams.csv");
                        FinalTeamSelection finalTeamSelection=new FinalTeamSelection();
                        finalTeamSelection.setCsvFile(fileForFinalTeamSelection);
                        finalTeamSelection.setTeamPlayerCount(playersCountPerTeam);
                        finalTeamSelection.setTotalFormedTeamsCount(totalFormedTeams);
                        finalTeamSelection.finalTeamsSelection(input);
                        break;
                    case 4:
                        FinalTeamSelection finalTeamSelection_2=new FinalTeamSelection();
                        finalTeamSelection_2.setTeamPlayerCount(playersCountPerTeam);
                        finalTeamSelection_2.reviewRandomlySelectedTeams(input);
                        break;
                    case 5:
                        FinalTeamSelection fts = new FinalTeamSelection();
                        if (playersCountPerTeam> 0) {
                            fts.viewFinalTeamsStatistics(playersCountPerTeam, totalFormedTeams, avgSkillValue, minAvg, maxAvg, remainingLeadersCount, remainingBalancersCount, remainingThinkersCount);
                        }else{
                            fts.viewFinalTeamsStatistics();
                        }
                        break;
                    case 6:
                        File file_1 = new File("possible_teams.csv");
                        if(!file_1.exists()){
                            System.out.println("⚠️ Please generate teams first.");
                            break;
                        }
                        ViewRemainingPlayers viewRemainingPlayers = new ViewRemainingPlayers();
                        viewRemainingPlayers.viewPlayersInCsvFile("possible_teams.csv");
                        break;
                    case 7:
                        File file = new File("possible_teams.csv");
                        if(!file.exists()){
                            System.out.println("⚠️ Please generate teams first.");
                            break;
                        }
//                        if (playersCountPerTeam == 0) {
//                            ReviewGeneratedTeams reviewGeneratedTeams = new ReviewGeneratedTeams(file);
//                            reviewGeneratedTeams.viewFormedTeams();
//                            break;
//                        }
                        File file_check = new File("files/possible_teams.csv");

                        if (file_check.exists()) {
//                            if(playersCountPerTeam>0){
                            ReviewGeneratedTeams vm = new ReviewGeneratedTeams(file);
                            vm.setTeamPlayerCount(playersCountPerTeam);
                            vm.viewFormedTeams();
//                            }
                        } else {
                            System.out.println("⚠️ No teams found. Please generate teams first.");
                        }
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

    public  void participantDashboard(Scanner input) {
        while (true) {
            try {
                System.out.println("\n\n" + "=".repeat(80));
                System.out.println("\n                          PARTICIPANT DASHBOARD\n");
                System.out.println("=".repeat(80));
                System.out.println("\n Welcome, " + currentUserName + "!");
                System.out.println("-".repeat(80));
                System.out.println("\n 1) Complete the Survey");
                System.out.println(" 2) View My Profile");
                System.out.println(" 3) Review assigned team");
                System.out.println(" 4) Logout\n");
                System.out.println("=".repeat(80));
                System.out.println("\n");

                int choice = getValidIntegerInput(input, "Enter your choice: ", 1, 4);

                switch (choice) {
                    case 1:
                        Participant player = new Participant(currentUserName, currentUserEmail);
                        player.participantSurvey(input);
                        break;
                    case 2:
                        System.out.println("\n📋 Your Profile:");
                        System.out.println("   Name: " + currentUserName);
                        System.out.println("   Email: " + currentUserEmail);
                        break;

                    case 3:
                        String csvFileName="formed_teams.csv";
                        ReviewGeneratedTeams reviewGeneratedTeams=new ReviewGeneratedTeams(csvFileName);
                        reviewGeneratedTeams.reviewParticipantAssignedTeam();
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
}
