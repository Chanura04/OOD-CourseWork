package main;

import java.io.*;

public class ReviewGeneratedTeams {
    private int teamPlayerCount=0;
    private File csvFilePath;
    private String csvFileName;

    public void setTeamPlayerCount(int teamPlayerCount) {
        this.teamPlayerCount = teamPlayerCount;
    }
    public int getTeamPlayerCount(){
        return teamPlayerCount;
    }

    public ReviewGeneratedTeams(String csvFileName){
        this.csvFileName=csvFileName;
    }

    public void viewFormedTeams(){

        if(teamPlayerCount==0){
            try(BufferedReader br=new BufferedReader((new FileReader(csvFileName)))) {

                String line;
                while ((line=br.readLine())!=null){
                    String[] columns = line.split(",");
                    System.out.printf("%-19s %-22s %-19s %-16s %-15s %-16s %-18s %-19s %-17s%n", columns[0], columns[1], columns[2], columns[3],columns[4], columns[5], columns[6], columns[7], columns[8]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            try (BufferedReader br = new BufferedReader((new FileReader(csvFileName)))) {
                br.readLine();
                String line;
                int teamNumber = 1;
                int lineNum = 0;

                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println("\t\t\t\t\t\t\t\t\tTeam " + teamNumber);
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------\n");

                System.out.printf("%-19s %-22s %-19s %-16s %-15s %-16s %-18s %-19s %-17s%n", "ID", "Name", "Email", "PreferredGame", "SkillLevel", "PreferredRole", "PersonalityScore", "PersonalityType", "TeamNumber");

                while ((line = br.readLine()) != null) {
                    lineNum++;

                    String[] columns = line.split(",");
                    System.out.printf("%-15s %-18s %-27s %-20s %-15s %-16s %-18s %-19s %-17s%n", columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7], columns[8]);

                    if (lineNum % getTeamPlayerCount() == 0 && br.ready()) {
                        teamNumber++;
                        System.out.println("\n\n");

                        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");
                        System.out.println("\t\t\t\t\t\t\t\t\tTeam " + teamNumber);
                        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------\n");

                        System.out.printf("%-19s %-22s %-19s %-16s %-15s %-16s %-18s %-19s %-17s%n", "ID", "Name", "Email", "PreferredGame", "SkillLevel", "PreferredRole", "PersonalityScore", "PersonalityType", "TeamNumber");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public  void reviewParticipantAssignedTeam() {
        File file = new File(csvFileName);
        try(BufferedReader br=new BufferedReader((new FileReader(file)))) {

            String line;
            while ((line=br.readLine())!=null){
                String[] columns = line.split(",");
                System.out.printf("%-15s %-18s %-27s %-20s %-15s %-16s %-18s %-19s %-17s%n", columns[0], columns[1], columns[2], columns[3],columns[4], columns[5], columns[6], columns[7],columns[8]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void viewPlayersInCsvFile(){
        try(BufferedReader br=new BufferedReader((new FileReader(csvFileName)))) {
            String line;
            while ((line=br.readLine())!=null){
                String[] columns = line.split(",");
                System.out.printf("%-15s %-18s %-27s %-20s %-15s %-16s %-18s %-19s%n", columns[0], columns[1], columns[2], columns[3],columns[4], columns[5], columns[6], columns[7]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
