package chooseTeams;

import java.util.ArrayList;
import java.util.Objects;


public class TeamMembersSelection {


    public void categorize_personality(){
        PlayerDataLoader playerDataLoader=new PlayerDataLoader();

        ArrayList<String> all_leaders=new ArrayList<>();
        ArrayList<String> all_balancers=new ArrayList<>();
        ArrayList<String> all_thinkers=new ArrayList<>();


        ArrayList<String> player_all_personalityType=new ArrayList<>();


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







        System.out.println("all_thinkers:"+all_thinkers);
        System.out.println("all_balancers:"+all_balancers);
        System.out.println("all_leaders:"+all_leaders);
        System.out.println(all_thinkers.size()+all_balancers.size()+all_leaders.size());

    }

}
