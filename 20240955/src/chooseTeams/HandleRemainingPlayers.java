package chooseTeams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HandleRemainingPlayers {
    private ArrayList<String> remaining_all_leaders = new ArrayList<>();
    private ArrayList<String> remaining_all_balancers = new ArrayList<>();
    private ArrayList<String> remaining_all_thinkers = new ArrayList<>();
    private boolean hasTeam=false;


    public ArrayList<ArrayList<String>> newTeams = new ArrayList<>();
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
//        while(!isTrue){
//            createNewTeams(avgSkillValue);
//        }
    }

    private void createNewTeams(double avgSkillValue) {
        double avg=avgSkillValue;
        int retryLimit = 50000000; // avoid infinite loop
        int retryCount = 0;
        Random rand = new Random();



        while (remaining_all_leaders.size() > 1 &&
                remaining_all_balancers.size() >= 2 &&
                remaining_all_thinkers.size() >= 2) {

            if (retryCount++ > retryLimit) {
                System.out.println("⚠️ Breaking loop due to too many retries (no valid teams left).");
                break;
            }

            ArrayList<String> newTeam = new ArrayList<>();
            int randomIndexForLeaders = rand.nextInt(remaining_all_leaders.size());
            int randomIndexForBalancers1 = rand.nextInt(remaining_all_balancers.size());
            int randomIndexForBalancers2 = rand.nextInt(remaining_all_balancers.size());
            while (randomIndexForBalancers2 == randomIndexForBalancers1 && remaining_all_balancers.size() > 1) {
                randomIndexForBalancers2 = rand.nextInt(remaining_all_balancers.size());
            }

            int randomIndexForThinkers1 = rand.nextInt(remaining_all_thinkers.size());
            int randomIndexForThinkers2 = rand.nextInt(remaining_all_thinkers.size());
            while (randomIndexForThinkers2 == randomIndexForThinkers1 && remaining_all_thinkers.size() > 1) {
                randomIndexForThinkers2 = rand.nextInt(remaining_all_thinkers.size());
            }


            newTeam.add(remaining_all_leaders.get(randomIndexForLeaders));
            newTeam.add(remaining_all_balancers.get(randomIndexForBalancers1));
            newTeam.add(remaining_all_balancers.get(randomIndexForBalancers2));
            newTeam.add(remaining_all_thinkers.get(randomIndexForThinkers1));
            newTeam.add(remaining_all_thinkers.get(randomIndexForThinkers2));


            if (!isThirdIndexValid(newTeam)) {
                continue; // Skip this team and try forming another
            }

            int teamSkillSum = calculateSkillSum(newTeam);

            double maxValue=avgSkillValue+3;
            double minValue=avgSkillValue-3;


            if (teamSkillSum >= minValue && teamSkillSum <= maxValue) {
                newTeams.add(newTeam);
                System.out.println("✅ New formed team (sum ≥ avg): " + newTeam);
                remaining_all_leaders.remove(newTeam.get(0));
                remaining_all_balancers.remove(newTeam.get(1));
                remaining_all_balancers.remove(newTeam.get(2));
                remaining_all_thinkers.remove(newTeam.get(3));
                remaining_all_thinkers.remove(newTeam.get(4));
                retryCount = 0;
              }



        }
        System.out.println("Remaining Leaders: " + remaining_all_leaders.size());
        System.out.println("Remaining Balancers: " + remaining_all_balancers.size());
        System.out.println("Remaining Thinkers: " + remaining_all_thinkers.size());
        System.out.println(remaining_all_balancers);
        System.out.println(remaining_all_thinkers);
        System.out.println(remaining_all_leaders);

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
        return true;
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
