package main;

import java.util.ArrayList;

public interface TeamSelection {

    void categorizeByPersonalityType();
    ArrayList<String> selectUniqueLeaders(int count);
    ArrayList<String> selectUniqueThinkers(int count);
    ArrayList<String> selectUniqueBalancers(int count);
    ArrayList<ArrayList<String>> createTeams();
    void filterTeamsBySkillAverageValue();
    boolean isGameCountValid(ArrayList<String> team);
    void writeFinalTeamsOnCsvFile();
    boolean validateTeam( ArrayList<String> team);
    void writeRemainingPlayerInCsvFile();

}
