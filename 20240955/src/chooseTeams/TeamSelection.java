package chooseTeams;

import java.util.ArrayList;

public interface TeamSelection {

    void categorizeByPersonalityType();
    void getLeader();
    void getThinker();
    void getBalancers();
    void createFiveMembersTeam();
    void checkEachFormedTeamSkillSum();
    void finalTeamsSelection();
    boolean isGameCountValid(ArrayList<String> team);
    void writeFinalTeamsOnCsvFile();

}
