package chooseTeams;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class TeamMembersSelection implements TeamSelection {
    private int teamPlayerCount;

    private ArrayList<String> all_leaders=new ArrayList<>();
    private ArrayList<String> all_balancers=new ArrayList<>();
    private ArrayList<String> all_thinkers=new ArrayList<>();

    private  ArrayList<String> cp_leaders=all_leaders;
    private  ArrayList<String> cp_balancers=all_balancers;
    private ArrayList<String> cp_thinkers=all_thinkers;

    private ArrayList<String> selectedBalancers=new ArrayList<>();
    private ArrayList<String> selectedThinkers=new ArrayList<>();
    private ArrayList<String> selectedLeaders=new ArrayList<>();

    private ArrayList<ArrayList<String>> selectedTeamsInFirstFilter=new ArrayList<>();
    private ArrayList<ArrayList<String>> finalTeamCombination=new ArrayList<>();

    private double average=0;
    private ArrayList<ArrayList<String>> remainingTeams=new ArrayList<>();

    private ArrayList<String> rest_leaders=new ArrayList<>();
    private ArrayList<String> rest_balancers=new ArrayList<>();
    private ArrayList<String> rest_thinkers=new ArrayList<>();


    // Constructor for normal usage
    public TeamMembersSelection(int teamPlayerCount) {
        this.teamPlayerCount = teamPlayerCount;


    }

    public double getAverage(){
        return average;
    }

    public ArrayList<ArrayList<String>> getCreatedTeams() {
        return createTeams();
    }

    

    public void categorizeByPersonalityType(){

        PlayerDataLoader playerDataLoader=new PlayerDataLoader();

        ArrayList<String> playerData = playerDataLoader.getPlayerData();

        for(int i=1;i<playerData.size();i++){
            String raw = playerData.get(i).replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");
            String personalityType = fields[7].trim();
            switch (personalityType.strip()) {
                case "Leader" -> all_leaders.add(playerData.get(i));
                case "Balanced" -> all_balancers.add(playerData.get(i));
                case "Thinker" -> all_thinkers.add(playerData.get(i));
            }
        }
    }

    public void checkSkillAverageValue(){
        PlayerDataLoader playerDataLoader=new PlayerDataLoader();

        ArrayList<String> playerData = playerDataLoader.getPlayerData();
        int totalSkillValue=0;
        for(int i=1;i<playerData.size();i++){
            String raw = playerData.get(i).replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");
            String getSkillValue = fields[4].trim();
            int skillValue=Integer.parseInt(getSkillValue);
            totalSkillValue+=skillValue;
        }
        float averageSkillValue= (float) totalSkillValue /playerData.size();
        System.out.println("averageSkillValue:"+averageSkillValue);

    }

    public ArrayList<String> selectUniqueLeaders(int count) {
        if (!(cp_leaders.size() < count)) {
            Random rand = new Random();
            selectedLeaders.clear();

            String leader = cp_leaders.get(rand.nextInt(cp_leaders.size()));
            selectedLeaders.add(leader);

        }
        return selectedLeaders;


//        System.out.println("Selected Leader: " + selectedLeaders);
}

    public ArrayList<String> selectUniqueThinkers(int count) {
        if (!(cp_thinkers.size() < count)) {
            Random rand = new Random();
            selectedThinkers.clear();

            while (selectedThinkers.size() < 2 && cp_thinkers.size() > selectedThinkers.size()) {
                String thinker = cp_thinkers.get(rand.nextInt(cp_thinkers.size()));
                if (!selectedThinkers.contains(thinker)) {
                    selectedThinkers.add(thinker);
                }
            }

        }
        return selectedThinkers;

//        System.out.println("Selected Thinkers: " + selectedThinkers);
    }

    public ArrayList<String> selectUniqueBalancers(int count) {
        if (!(cp_balancers.size() < count)) {
            Random rand = new Random();
            selectedBalancers.clear();
//        int balancerCount=teamPlayerCount-3;


            while (selectedBalancers.size() < count && cp_balancers.size() > selectedBalancers.size() ) {
                String balancer = cp_balancers.get(rand.nextInt(cp_balancers.size()));
                if (!selectedBalancers.contains(balancer)) {
                    selectedBalancers.add(balancer);
                }
            }

        }
        return selectedBalancers;

//        System.out.println("Selected Balancers: " + selectedBalancers);
    }

    // Field to store all teams
    private final ArrayList<ArrayList<String>> allTeams = new ArrayList<>();

    public ArrayList<ArrayList<String>> createTeams() {
        categorizeByPersonalityType();
        int leaderCount = 1;
        int thinkerCount = 2;
        int balancerCount = teamPlayerCount - 3;

        // keep forming teams while there are enough people
        while (cp_thinkers.size() >= 2 && cp_balancers.size() >= 2 && cp_leaders.size() >= 1) {
            // Clear per-selection lists
            selectedLeaders.clear();
            selectedThinkers.clear();
            selectedBalancers.clear();

            // pick members

            selectUniqueLeaders(leaderCount);     // should fill selectedLeaders with 1 element
            selectUniqueBalancers(balancerCount);  // should fill selectedBalancers with 2 elements

            selectUniqueThinkers(thinkerCount);    // should fill selectedThinkers with 2 elements

            // create a proper team list (NOT a single concatenated string)
            ArrayList<String> team = new ArrayList<>();
            team.addAll(selectedLeaders);
            team.addAll(selectedBalancers);
            team.addAll(selectedThinkers);

            // ensure it is exactly 5 members before storing
            if (team.size() == teamPlayerCount) {
                if(!isGameCountValid(team)){
//                    System.out.println("Team is not valid: " + team);
                    continue;
                }
                allTeams.add(team);
//                System.out.println("Created Team: " + team);

            } else {
                // handle unexpected selection problems (log and break to avoid infinite loops)
                System.err.println("Unexpected team size: " + team.size());
                break;
            }

            // remove selected members from the original pools so they can't be reused
            cp_leaders.removeAll(selectedLeaders);
            cp_balancers.removeAll(selectedBalancers);
            cp_thinkers.removeAll(selectedThinkers);
        }


        System.out.println("\nAll teams created: " + allTeams.size());


        System.out.println("\nAll Teams Formed: " + allTeams);
        System.out.println("Total Teams: " + allTeams.size());
        System.out.println("Remaining Leaders: " + cp_leaders);
        System.out.println("leaders.size():" + cp_leaders.size());
        System.out.println("Remaining Balancers: " + cp_balancers);
        System.out.println("Balancers.size():" + cp_balancers.size());
        System.out.println("Remaining Thinker: " + cp_thinkers);
        System.out.println("Thinkers.size():" + cp_thinkers.size());
        formTeamsBySkillAverageValue();
        finalTeamsSelection();
        return allTeams;
    }
//Maximum 2 from same game per team
    public boolean isGameCountValid(ArrayList<String> team) {
        HashMap<String, Integer> countMap = new HashMap<>();

        for (String player : team) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            if (fields.length > 3) {
                String thirdValue = fields[3].trim();
                countMap.put(thirdValue, countMap.getOrDefault(thirdValue, 0) + 1);
                if (countMap.get(thirdValue) > 2) {
                    return false;
                }
            }
        }
        return true;
    }

    public void formTeamsBySkillAverageValue(){
        int totalSum =0;
        int teamSize=allTeams.size();
        for(int i=0;i<allTeams.size();i++){
            ArrayList<String> team=allTeams.get(i);
            int teamSkillSum=0;
            for(int j=0;j<team.size();j++){
                String raw = team.get(j).replace("[", "").replace("]", "").trim();
                String[] fields = raw.split(",");
                String getSkillValue = fields[4].trim();
                int skillValue=Integer.parseInt(getSkillValue);
                teamSkillSum+=skillValue;
            }
            System.out.println("Team "+(i+1)+" skill sum:"+teamSkillSum);
            totalSum +=teamSkillSum;

        }
        System.out.println(teamSize);
        System.out.println("Total skill average:"+totalSum / teamSize);


        average = (double) totalSum / teamSize;




        for(int i=0;i<allTeams.size();i++){
            ArrayList<String> team=allTeams.get(i);
            int teamSkillSum=0;
            for(int j=0;j<team.size();j++){
                String raw = team.get(j).replace("[", "").replace("]", "").trim();
                String[] fields = raw.split(",");
                String getSkillValue = fields[4].trim();
                int skillValue=Integer.parseInt(getSkillValue);
                teamSkillSum+=skillValue;
            }

            double maxValue=average+3;
            double minValue=average-3;

            if( teamSkillSum >= minValue && teamSkillSum <= maxValue){
                selectedTeamsInFirstFilter.add(team);
            }else{
                remainingTeams.add(team);
            }
        }
        System.out.println("Selected Teams In First Filter:"+selectedTeamsInFirstFilter);
        System.out.println("selectedTeamsInFirstFilter.size():"+selectedTeamsInFirstFilter.size());

        System.out.println("Remaining Teams:"+remainingTeams);
        System.out.println("remainingTeams.size():"+remainingTeams.size());


//        HandleRemainingPlayers handleRemainingPlayers=new HandleRemainingPlayers();


    }
    public void finalTeamsSelection(){
        ArrayList<String> remainingPlayers = new ArrayList<>();
        ArrayList<String> combinedRemainingPlayers = new ArrayList<>();


        remainingPlayers.addAll(cp_leaders);
        remainingPlayers.addAll(cp_balancers);
        remainingPlayers.addAll(cp_thinkers);

        for (ArrayList<String> team : remainingTeams) {
            combinedRemainingPlayers.addAll(team);
        }

// Add your single remaining players
        combinedRemainingPlayers.addAll(remainingPlayers);

        System.out.println("Total combined remaining players: " + combinedRemainingPlayers.size());
        System.out.println("Combined remaining players: " + combinedRemainingPlayers.get(2));

        HandleRemainingPlayers handleRemainingPlayers=new HandleRemainingPlayers(teamPlayerCount,combinedRemainingPlayers, average);
        System.out.println("\n\n\n\n\n");
        ArrayList<ArrayList<String>> handledRemainingTeams=handleRemainingPlayers.getCreatedTeams();

        rest_leaders=handleRemainingPlayers.getRemaining_all_leaders();
        rest_balancers=handleRemainingPlayers.getRemaining_all_balancers();
        rest_thinkers=handleRemainingPlayers.getRemaining_all_thinkers();

        System.out.println("Handled teams are: ");
        finalTeamCombination.addAll(handledRemainingTeams);
        finalTeamCombination.addAll(selectedTeamsInFirstFilter);
        System.out.println("Final teams combination: "+finalTeamCombination);
        System.out.println("Final teams combination size: "+finalTeamCombination.size());

        writeFinalTeamsOnCsvFile();
        writeRemainingPlayerInCsvFile();
    }

    public void writeFinalTeamsOnCsvFile(){
        int teamNumber=0;
        try (FileWriter writer = new FileWriter("output_result.csv")) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType,TeamNumber");
            writer.write("\n");

            for (ArrayList<String> group : finalTeamCombination) {
                teamNumber++;
                for (String row : group) {
                    String no=","+teamNumber;
                    row = row.replace("[", "").replace("]", no).trim();

//                    writer.write(String.join(",", row));
                    writer.write(row);

                    writer.write("\n");
                }
            }
            System.out.println("CSV file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRemainingPlayerInCsvFile(){

        ArrayList<String> restRemainingPlayers = new ArrayList<>();

// Combine all remaining players
        restRemainingPlayers.addAll(rest_leaders);
        restRemainingPlayers.addAll(rest_balancers);
        restRemainingPlayers.addAll(rest_thinkers);

        System.out.println("Rest remaining players: " + restRemainingPlayers);

        try (FileWriter writer = new FileWriter("remaining_players.csv")) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");

            // Write each player as a new line
            for (String player : restRemainingPlayers) {
                player = player.replace("[", "").replace("]", "").trim();
                writer.write(player);
                writer.write("\n");
            }

            System.out.println("CSV file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }





}









//need to implement
//merge remainigselected palyers with this
//Ensure at least 3 different roles per team (more if team size > 5).






















//
//
//// number of teams
//int teamsCount = allTeams.size();
//
//// get 1st team's 3rd member (indexing from 0)
//String member = allTeams.get(0).get(2);
//
//// iterate and print nicely
//for (int i = 0; i < allTeams.size(); i++) {
//        System.out.println("Team " + (i + 1) + ":");
//ArrayList<String> t = allTeams.get(i);
//    for (int j = 0; j < t.size(); j++) {
//        System.out.println("  member " + (j + 1) + ": " + t.get(j));
//        }
//        }
