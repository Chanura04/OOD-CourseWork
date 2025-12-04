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




    //sq 1.2
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

                int choice = getValidIntegerInput(input, "Enter your choice: ", 1, 8);//2.1

                switch (choice) {
                    case 1:
                        HandleDataCsvFiles handleDataCsvFiles = new HandleDataCsvFiles();//2.1
                        handleDataCsvFiles.dataFileImport(input);//2.2
                        uploadCsvFileName = handleDataCsvFiles.getUploadCsvFileName();
                        break;
                    case 2://sq 2-Initiate team formations, define team size
                        if (uploadCsvFileName == null) {
                            System.out.println("⚠️ Please import participant data first before generating teams.");
                            break;
                        }
                        TeamMembersSelection teamMembersSelection = new TeamMembersSelection(uploadCsvFileName);//sq 2.1-Initiate team formations
                        teamMembersSelection.defineTeamSize(input);//sq 2.2 -Define team size
                        teamMembersSelection.generateTeams(input);//sq 2.2 -Initiate team formations
                        playersCountPerTeam=teamMembersSelection.getTeamPlayerCount();//2.2.25
                        totalFormedTeams=teamMembersSelection.getTotalFinalTeamCombination();//2.2.26
                        avgSkillValue = teamMembersSelection.getAverage();//2.2.27
                        minAvg = teamMembersSelection.getMinimumSkillAverage();//2.2.28
                        maxAvg = teamMembersSelection.getMaximumSkillAverage();//2.2.29
                        remainingBalancersCount=teamMembersSelection.getRemainingBalancersCount();//2.2.30
                        remainingLeadersCount=teamMembersSelection.getRemainingLeadersCount();//2.2.31
                        remainingThinkersCount=teamMembersSelection.getRemainingThinkersCount();//2.2.32
                        break;
                    case 3:
                        File fileForFinalTeamSelection=new File("possible_teams.csv");
                        FinalTeamSelection finalTeamSelection=new FinalTeamSelection();//2.1
                        finalTeamSelection.setCsvFile(fileForFinalTeamSelection);//2.2
                        finalTeamSelection.setTeamPlayerCount(playersCountPerTeam);//2.3
                        finalTeamSelection.setTotalFormedTeamsCount(totalFormedTeams);//2.4
                        finalTeamSelection.finalTeamsSelection(input);//2.5
                        break;
                    case 4:
                        File f = new File("formed_teams.csv");
                        if(!f.exists()){//2.2
                            System.out.println("⚠️ Please generate final teams first.");
                            break;//2.2.1
                        }
                        FinalTeamSelection finalTeamSelection_2=new FinalTeamSelection();//2.3
                        finalTeamSelection_2.setTeamPlayerCount(playersCountPerTeam);//2.4
                        finalTeamSelection_2.reviewRandomlySelectedTeams(input);//2.5
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
                        ReviewGeneratedTeams rgt = new ReviewGeneratedTeams("remaining_players.csv");
                        rgt.viewPlayersInCsvFile();
                        break;
                    case 7:
                        File file = new File("possible_teams.csv");
                        if(!file.exists()){//2.2
                            System.out.println("⚠️ No teams found. Please generate teams first.");
                            break;
                        }
                         ReviewGeneratedTeams vm = new ReviewGeneratedTeams("possible_teams.csv");//2.3
                         vm.setTeamPlayerCount(playersCountPerTeam);//2.4
                         vm.viewFormedTeams();//2.5
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
    //sq 1.2 complete survey
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
                        Participant player = new Participant(currentUserName, currentUserEmail);//sq 2.2 complete survey
                        player.participantSurvey(input);  //sq 2.3 complete survey
                        break;
                    case 2:
                        System.out.println("\n📋 Your Profile:");
                        System.out.println("   Name: " + currentUserName);
                        System.out.println("   Email: " + currentUserEmail);
                        break;

                    case 3:
                        File file = new File("formed_teams.csv");

                        if(!file.exists()){//2.2
                            System.out.println("⚠️ Please generate teams first.");
                            break;
                        }
                        String csvFileName="formed_teams.csv";
                        ReviewGeneratedTeams reviewGeneratedTeams=new ReviewGeneratedTeams(csvFileName);//2.3
                        reviewGeneratedTeams.reviewParticipantAssignedTeam();//2.4
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



    private  int getValidIntegerInput(Scanner input, String prompt, int min, int max) {
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
