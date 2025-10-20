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

        // Once categorized, you can call a new method to create new teams:
        createNewTeams(avgSkillValue);
    }

    private void createNewTeams(double avgSkillValue) {
        ArrayList<ArrayList<String>> newTeams = new ArrayList<>();
        Random rand = new Random();

        while (remaining_all_leaders.size() >= 1 &&
                remaining_all_balancers.size() >= 2 &&
                remaining_all_thinkers.size() >= 2) {

            ArrayList<String> newTeam = new ArrayList<>();

            newTeam.add(remaining_all_leaders.remove(rand.nextInt(remaining_all_leaders.size())));
            newTeam.add(remaining_all_balancers.remove(rand.nextInt(remaining_all_balancers.size())));
            newTeam.add(remaining_all_balancers.remove(rand.nextInt(remaining_all_balancers.size())));
            newTeam.add(remaining_all_thinkers.remove(rand.nextInt(remaining_all_thinkers.size())));
            newTeam.add(remaining_all_thinkers.remove(rand.nextInt(remaining_all_thinkers.size())));

            // ✅ Verify "same value in third index" condition
            if (!isThirdIndexValid(newTeam)) {
                System.out.println("⚠️ Discarded team (more than 2 players share same 3rd index): " + newTeam);
                continue; // Skip this team and try forming another
            }

            int teamSkillSum = calculateSkillSum(newTeam);

            if (teamSkillSum >= avgSkillValue) {
                newTeams.add(newTeam);
                System.out.println("✅ New formed team (sum ≥ avg): " + newTeam);
            } else {
                System.out.println("⚠️ Discarded team (below avg): " + teamSkillSum);
            }
        }

        System.out.println("\nTotal new teams formed from remaining players: " + newTeams.size());
    }
    private boolean isThirdIndexValid(ArrayList<String> team) {
        HashMap<String, Integer> countMap = new HashMap<>();

        for (String player : team) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            if (fields.length > 3) {
                String thirdValue = fields[3].trim();
                countMap.put(thirdValue, countMap.getOrDefault(thirdValue, 0) + 1);
                if (countMap.get(thirdValue) > 2) {
                    return false; // ❌ More than 2 players with same third index value
                }
            }
        }
        return true; // ✅ Valid team
    }


    private int calculateSkillSum(ArrayList<String> team) {
        int sum = 0;
        for (String player : team) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");
            if (fields.length < 8) continue;
            int skillValue = Integer.parseInt(fields[4].trim());
            sum += skillValue;
        }
        return sum;
    }
}
