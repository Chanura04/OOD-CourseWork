package chooseTeams;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


public class TeamMembersSelection {
    private ArrayList<String> all_leaders=new ArrayList<>();
    private ArrayList<String> all_balancers=new ArrayList<>();
    private ArrayList<String> all_thinkers=new ArrayList<>();

    public TeamMembersSelection() {
        categorizeByPersonalityType();
    }

    public void categorizeByPersonalityType(){
        PlayerDataLoader playerDataLoader=new PlayerDataLoader();



        ArrayList<String> playerData = playerDataLoader.getPlayerData();
        System.out.println(playerData);


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
    public void getLeader(){
        categorizeByPersonalityType();

        ArrayList<String> cp_leaders=all_leaders;
        ArrayList<String> cp_balancers=all_balancers;



        ArrayList<String> selectedBalancers=new ArrayList<>();
        ArrayList<String> selectedLeaders=new ArrayList<>();
        Random rand = new Random();


        String getRandomValueForLeader= cp_leaders.get(rand.nextInt(cp_leaders.size()));



        System.out.println("getRandomValueForLeader:"+getRandomValueForLeader);


    }

    public void getThinker(){
        int thinkersCount=2;
//        categorizeByPersonalityType();
        ArrayList<String> cp_thinkers=all_thinkers;
        ArrayList<String> selectedThinkers=new ArrayList<>();

        Random rand = new Random();
        int track=1;

        while(track<=thinkersCount){
            String getRandomValueForThinker = cp_thinkers.get(rand.nextInt(cp_thinkers.size()));
            if( !selectedThinkers.contains(getRandomValueForThinker)) {
                selectedThinkers.add(getRandomValueForThinker);
                track++;
            }else{
                getThinker();
            }
        }
        System.out.println("getRandomValueForThinker:"+selectedThinkers.toString());
    }


}
