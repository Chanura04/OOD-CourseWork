package chooseTeams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.io.*;
import java.util.*;
import java.io.*;
import java.util.*;

public class FinalTeamSelection {
    private int requiredTeamCount;
    private int totalFormedTeamsCount;
    private File csvFile;
    private int teamPlayerCount;


    public void setTeamPlayerCount(int teamPlayerCount) {
        this.teamPlayerCount = teamPlayerCount;
    }

    public void setRequiredTeamCount(int requiredTeamCount) {
        this.requiredTeamCount = requiredTeamCount;
    }

    public void setTotalFormedTeamsCount(int totalFormedTeamsCount) {
        this.totalFormedTeamsCount = totalFormedTeamsCount;
    }

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }

    public void finalResult() {
        // Generate random team numbers using ArrayList
        ArrayList<ArrayList<String>> allTeams = new ArrayList<>();
        ArrayList<Integer> selectedTeamsNumbers = getRandomTeams(requiredTeamCount, totalFormedTeamsCount);
        System.out.println("Selected Teams: " + selectedTeamsNumbers);

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
                        ArrayList<String> playerData = new ArrayList<>();  // Move inside this loop!
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


}