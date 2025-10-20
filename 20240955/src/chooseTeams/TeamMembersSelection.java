package chooseTeams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;


public class TeamMembersSelection {

    private ArrayList<String> all_leaders=new ArrayList<>();
    private ArrayList<String> all_balancers=new ArrayList<>();
    private ArrayList<String> all_thinkers=new ArrayList<>();


    private  ArrayList<String> cp_leaders=all_leaders;
    private  ArrayList<String> cp_balancers=all_balancers;
    private ArrayList<String> cp_thinkers=all_thinkers;

    private ArrayList<String> selectedBalancers=new ArrayList<>();
    private ArrayList<String> selectedThinkers=new ArrayList<>();
    private ArrayList<String> selectedLeaders=new ArrayList<>();



    public TeamMembersSelection() {
        categorizeByPersonalityType();
    }

    public void categorizeByPersonalityType(){
        PlayerDataLoader playerDataLoader=new PlayerDataLoader();



        ArrayList<String> playerData = playerDataLoader.getPlayerData();
//        System.out.println(playerData);


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

//        System.out.println("all_thinkers:"+all_thinkers);
//        System.out.println("all_balancers:"+all_balancers);
//        System.out.println("all_leaders:"+all_leaders);
//        System.out.println(all_thinkers.size()+all_balancers.size()+all_leaders.size());

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
// Maximum two from the same game for per team
    public void getLeader() {
        Random rand = new Random();
        selectedLeaders.clear();

        String leader = cp_leaders.get(rand.nextInt(cp_leaders.size()));
        selectedLeaders.add(leader);

//        System.out.println("Selected Leader: " + selectedLeaders);
}

    public void getThinker() {
        Random rand = new Random();
        selectedThinkers.clear();

        while (selectedThinkers.size() < 2 && cp_thinkers.size() > selectedThinkers.size()) {
            String thinker = cp_thinkers.get(rand.nextInt(cp_thinkers.size()));
            if (!selectedThinkers.contains(thinker)) {
                selectedThinkers.add(thinker);
            }
        }
//        System.out.println("Selected Thinkers: " + selectedThinkers);
    }

    public void getBalancers() {
        Random rand = new Random();
        selectedBalancers.clear();

        while (selectedBalancers.size() < 2 && cp_balancers.size() > selectedBalancers.size()) {
            String balancer = cp_balancers.get(rand.nextInt(cp_balancers.size()));
            if (!selectedBalancers.contains(balancer)) {
                selectedBalancers.add(balancer);
            }
        }
//        System.out.println("Selected Balancers: " + selectedBalancers);
    }

    // Field to store all teams
    private ArrayList<ArrayList<String>> allTeams = new ArrayList<>();

    public void createFiveMembersTeam() {
        // keep forming teams while there are enough people
        while (cp_thinkers.size() >= 2 && cp_balancers.size() >= 2 && cp_leaders.size() >= 1) {
            // Clear per-selection lists
            selectedLeaders.clear();
            selectedThinkers.clear();
            selectedBalancers.clear();

            // pick members (use your corrected getLeader/getThinker/getBalancers methods)
            getLeader();     // should fill selectedLeaders with 1 element
            getBalancers();  // should fill selectedBalancers with 2 elements
            getThinker();    // should fill selectedThinkers with 2 elements

            // create a proper team list (NOT a single concatenated string)
            ArrayList<String> team = new ArrayList<>();
            team.addAll(selectedLeaders);
            team.addAll(selectedBalancers);
            team.addAll(selectedThinkers);

            // ensure it is exactly 5 members before storing
            if (team.size() == 5) {
                allTeams.add(team);
                System.out.println("Created Team: " + team);
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
        System.out.println("Remaining Balancers: " + cp_balancers);
        System.out.println("Remaining Thinker: " + cp_thinkers);
    }


}
