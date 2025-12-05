package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.logging.*;


public class FinalTeamSelection {
    private int requiredTeamCount;
    private int totalFormedTeamsCount;
    private File csvFile;
    private int playersCountPerTeam;
    double averageSkillLevel;
    double maximumSkillAverage;
    double minimumSkillAverage;
    int remainingLeadersCount;
    int remainingBalancersCount;
    int remainingThinkersCount;
    private static final Logger logger = Logger.getLogger(FinalTeamSelection.class.getName());
    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }
    public void setTeamPlayerCount(int playersCountPerTeam) {
        this.playersCountPerTeam = playersCountPerTeam;
    }
    public void setTotalFormedTeamsCount(int totalFormedTeamsCount) {
        this.totalFormedTeamsCount = totalFormedTeamsCount;
    }

    //Randomly selected all teams display and save to a csv file
    public void finalResult() {

        ArrayList<ArrayList<String>> allTeams = new ArrayList<>();
        ArrayList<Integer> selectedTeamsNumbers = getRandomTeams(requiredTeamCount, totalFormedTeamsCount);
        System.out.println("Selected Teams: " + selectedTeamsNumbers);
        logger.info("Selected Final Teams: " + selectedTeamsNumbers);

        ArrayList<String[]> allPlayers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip header
            String line;

            // Read all player data into ArrayList
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                allPlayers.add(columns);
            }

            // Print selected teams
            for (int team : selectedTeamsNumbers) {
                printTeamHeader(team);
                for (String[] columns : allPlayers) {
                    int teamNumber = Integer.parseInt(columns[8]);
                    if (teamNumber == team) {
                        ArrayList<String> playerData = new ArrayList<>();
                        playerData.add(columns[0]);
                        playerData.add(columns[1]);
                        playerData.add(columns[2]);
                        playerData.add(columns[3]);
                        playerData.add(columns[4]);
                        playerData.add(columns[5]);
                        playerData.add(columns[6]);
                        playerData.add(columns[7]);
                        playerData.add(columns[8]);
                        allTeams.add(playerData);  // Add the player row

                        System.out.printf("%-15s %-18s %-27s %-20s %-15s %-16s %-18s %-19s %-17s%n",
                                columns[0], columns[1], columns[2], columns[3], columns[4],
                                columns[5], columns[6], columns[7], columns[8]);
                    }
                }
                System.out.println("\n\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter("formed_teams.csv")) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType,TeamNumber");
            writer.write("\n");

            for (ArrayList<String> group : allTeams) {
                // Join all fields with commas and write as ONE line
                writer.write(String.join(",", group));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //Get random teams from 1 to max number of teams
    private ArrayList<Integer> getRandomTeams(int count, int max) {
        Random random = new Random();
        ArrayList<Integer> teams = new ArrayList<>();
        while (teams.size() < count) {
            int num = random.nextInt(max) + 1; // 1 to max
            if (!teams.contains(num)) {
                teams.add(num);
            }
        }
        return teams;
    }

    private void printTeamHeader(int teamNumber) {
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("\t\t\t\t\t\t\t\t\tTeam " + teamNumber);
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.printf("%-19s %-22s %-19s %-16s %-15s %-16s %-18s %-19s %-17s%n",
                "ID", "Name", "Email", "PreferredGame", "SkillLevel", "PreferredRole",
                "PersonalityScore", "PersonalityType", "TeamNumber");
    }

    //Display all formed teams from csv file
    public void viewFormedTeams(File finalSelectedTeams){

        try(BufferedReader br=new BufferedReader((new FileReader(finalSelectedTeams)))) {
            br.readLine();
            String line;
            int lineNum=0;

            line = br.readLine();
            if (line != null) {
                String[] firstColumns = line.split(",");
                int teamNumber = Integer.parseInt(firstColumns[8]);

                // Print first team header
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println( "\t\t\t\t\t\t\t\t\tTeam " + teamNumber );
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------\n");
                System.out.printf("%-19s %-22s %-19s %-16s %-15s %-16s %-18s %-19s %-17s%n", "ID","Name","Email","PreferredGame","SkillLevel","PreferredRole","PersonalityScore","PersonalityType","TeamNumber");

                // Print first player
                System.out.printf("%-15s %-18s %-27s %-20s %-15s %-16s %-18s %-19s %-17s%n", firstColumns[0], firstColumns[1], firstColumns[2], firstColumns[3], firstColumns[4], firstColumns[5], firstColumns[6], firstColumns[7], firstColumns[8]);
                lineNum++;

                // Continue reading
                while ((line=br.readLine())!=null){
                    lineNum++;
                    String[] columns = line.split(",");

                    // Check if team number changed
                    int currentTeamNumber = Integer.parseInt(columns[8]);
                    if (currentTeamNumber != teamNumber) {
                        teamNumber = currentTeamNumber;
                        System.out.println("\n\n");
                        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");
                        System.out.println( "\t\t\t\t\t\t\t\t\tTeam " + teamNumber );
                        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------\n");
                        System.out.printf("%-19s %-22s %-19s %-16s %-15s %-16s %-18s %-19s %-17s%n", "ID","Name","Email","PreferredGame","SkillLevel","PreferredRole","PersonalityScore","PersonalityType","TeamNumber");
                    }
                    System.out.printf("%-15s %-18s %-27s %-20s %-15s %-16s %-18s %-19s %-17s%n", columns[0], columns[1], columns[2], columns[3],columns[4], columns[5], columns[6], columns[7],columns[8]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Get the input for the required teams count from the user
    public void finalTeamsSelection(Scanner input){
        //2.6
        if(!getPreviousStaticData()){
            System.out.println("⚠️ Please generate teams first.");
            return;
        }
        getPreviousStaticData();//2.7
        File file_check = new File("possible_teams.csv");
        //2.8
        if (!file_check.exists()) {
            System.out.println("⚠️ No teams found. Please generate teams first.");
            return;
        }

        System.out.println("\n📊 Total teams formed: " + totalFormedTeamsCount);

        try{//2.9
            requiredTeamCount = getValidIntegerInput(input,
                    "\nEnter required teams count (minimum 1): ", 1, totalFormedTeamsCount);
            finalResult();//3.2
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    //Get stored static data from the csv file.For maintain the previous data memory
    public boolean getPreviousStaticData() {

        String basePath = System.getProperty("user.dir");
        String filePath = basePath + File.separator + "files" + File.separator + "StaticData.csv";

        File logFile = new File(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            br.readLine(); // skip header
            String line, lastLine = null;
            while ((line = br.readLine()) != null) {
                lastLine = line;
            }
            if (lastLine != null && !lastLine.trim().isEmpty()) {
                String[] columns = lastLine.split(",");
                if (columns.length >= 8) { // ensure all expected columns exist
                    playersCountPerTeam = Integer.parseInt(columns[0]);
                    totalFormedTeamsCount = Integer.parseInt(columns[1]);
                    averageSkillLevel = Double.parseDouble(columns[2]);
                    maximumSkillAverage = Double.parseDouble(columns[3]);
                    minimumSkillAverage = Double.parseDouble(columns[4]);
                    remainingLeadersCount = Integer.parseInt(columns[5]);
                    remainingBalancersCount = Integer.parseInt(columns[6]);
                    remainingThinkersCount = Integer.parseInt(columns[7]);
                    return true;
                }
            }
            return false;
        } catch (IOException | NumberFormatException e) {
            return false;
        }
    }


    //Check formed teams acceptance from the user
    public  void reviewRandomlySelectedTeams(Scanner input) {

        File file = new File("formed_teams.csv");
        viewFormedTeams(file);//2.6

        //2.7
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
            playersCountPerTeam = 0;
            System.out.println("❌ Teams were not accepted. Export cancelled.");
            File file1 = new File("formed_teams.csv");
            if (file1.exists()) {
                HandleDataCsvFiles handleDataCsvFiles = new HandleDataCsvFiles();//3.3
                handleDataCsvFiles.deleteCsvFile(file1);//3.4
            }
        }
    }

    private  boolean getValidResponseInput(Scanner input, String prompt, String y, String n) {
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
    public  void viewFinalTeamsStatistics(
            int teamPlayerCount,
            int totalTeamsCount,
            double averageSkillLevel,
            double maximumSkillAverage,
            double minimumSkillAverage,
            int remainingLeadersCount,
            int remainingBalancersCount,
            int remainingThinkersCount
    ) {

        if(!getPreviousStaticData()){
            System.out.println("⚠️ Please generate teams first.");
            return;
        }
        getPreviousStaticData();
        File file = new File("possible_teams.csv");
        if (!file.exists()) {
            System.out.println("⚠️ No teams found. Please generate teams first.");
        }

        System.out.println("\n\n" + "=".repeat(80));
        System.out.println("\n                          TEAM STATISTICS");
        System.out.println("=".repeat(80));
        System.out.println("\n📊 Total teams formed: " + totalTeamsCount);
        System.out.println("Player Count per Team: " + teamPlayerCount);
        System.out.printf("Average Skill Value: %.2f\n",averageSkillLevel );
        System.out.printf("Formed Teams Skill Range: %.2f - %.2f\n", minimumSkillAverage, maximumSkillAverage );
        System.out.println("-".repeat(80));
        System.out.println("Remaining Players by Type:");
        System.out.println("  Leaders: " + remainingLeadersCount);
        System.out.println("  Balancers: " + remainingBalancersCount);
        System.out.println("  Thinkers: " + remainingThinkersCount);
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("\n");
    }

    public  void viewFinalTeamsStatistics() {

        if(!getPreviousStaticData()){
            System.out.println("⚠️ Please generate teams first.");
            return;
        }
        getPreviousStaticData();
        File file = new File("possible_teams.csv");
        if (!file.exists()) {
            System.out.println("⚠️ No teams found. Please generate teams first.");
        }

        System.out.println("\n\n" + "=".repeat(80));
        System.out.println("\n                          TEAM STATISTICS");
        System.out.println("=".repeat(80));
        System.out.println("\n📊 Total teams formed: " + totalFormedTeamsCount);
        System.out.println("Player Count per Team: " + playersCountPerTeam);
        System.out.printf("Average Skill Value: %.2f\n",averageSkillLevel );
        System.out.printf("Formed Teams Skill Range: %.2f - %.2f\n", minimumSkillAverage, maximumSkillAverage );
        System.out.println("-".repeat(80));
        System.out.println("Remaining Players by Type:");
        System.out.println("  Leaders: " + remainingLeadersCount);
        System.out.println("  Balancers: " + remainingBalancersCount);
        System.out.println("  Thinkers: " + remainingThinkersCount);
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("\n");
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