package chooseTeams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HandleRemainingPlayers {
    private ArrayList<String> remaining_all_leaders = new ArrayList<>();
    private ArrayList<String> remaining_all_balancers = new ArrayList<>();
    private ArrayList<String> remaining_all_thinkers = new ArrayList<>();

    public void remainingTeamsCategorizeByPersonalityType(ArrayList<String> remainingPlayers, double avgSkillValue) {

        for (String player : remainingPlayers) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            // Defensive check for array length
            if (fields.length < 8) continue;

            String personalityType = fields[7].trim();

            switch (personalityType) {
                case "Leader" -> remaining_all_leaders.add(player);
                case "Balanced" -> remaining_all_balancers.add(player);
                case "Thinker" -> remaining_all_thinkers.add(player);
            }
        }

        System.out.println("Remaining Leaders: " + remaining_all_leaders.size());
        System.out.println("Remaining Balancers: " + remaining_all_balancers.size());
        System.out.println("Remaining Thinkers: " + remaining_all_thinkers.size());


        createNewTeams(avgSkillValue);
    }


}
