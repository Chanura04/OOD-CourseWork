package chooseTeams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HandleRemainingPlayers extends TeamMembersSelection {

    //Passed remaining players for the second filtering process
    private final ArrayList<String> remainingPlayers;

    private final double avgSkillValue;

    //Passed players stored separately using a personality type
    private final ArrayList<String> remaining_all_leaders = new ArrayList<>();
    private final ArrayList<String> remaining_all_balancers = new ArrayList<>();
    private final ArrayList<String> remaining_all_thinkers = new ArrayList<>();

    private final int teamPlayerCount;
    private final String file_name;

    private final Random rand = new Random();

    //Formed teams in the second filtering process
    public ArrayList<ArrayList<String>> selectedTeamsInSecondFilter = new ArrayList<>();

    public HandleRemainingPlayers(int teamPlayerCount,ArrayList<String> remainingPlayers, double avgSkillValue,String file_name) {
        super(teamPlayerCount,file_name);
        this.teamPlayerCount = teamPlayerCount;
        this.remainingPlayers = remainingPlayers;
        this.file_name=file_name;
        this.avgSkillValue=avgSkillValue;
        System.out.println("Avg Skill Value:"+avgSkillValue);
        System.out.println("Remaining Players:"+remainingPlayers);
        categorizeByPersonalityType();

    }

    public ArrayList<String> getRemaining_all_leaders() {
        return remaining_all_leaders;
    }
    public ArrayList<String> getRemaining_all_balancers() {
        return remaining_all_balancers;
    }
    public ArrayList<String> getRemaining_all_thinkers() {
        return remaining_all_thinkers;
    }


    @Override
    public void categorizeByPersonalityType() {

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


    }
    public ArrayList<ArrayList<String>> getCreatedTeams() {
        return createTeams();
    }

    @Override
    public boolean validateTeam( ArrayList<String> team){
        int leaderCount=0;
        int balancerCount=0;
        int thinkerCount=0;
        boolean isTeamValid=false;
        ArrayList<String> uniqueRoles=new ArrayList<>();
        for (String player : team) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            // Defensive check for array length
            if (fields.length < 8) continue;

            String personalityType = fields[7].trim();
            String role=fields[5].trim();
            if(!uniqueRoles.contains(role)){
                uniqueRoles.add(role);
            }

            switch (personalityType) {
                case "Leader" -> ++leaderCount;
                case "Balanced" -> ++balancerCount;
                case "Thinker" -> ++thinkerCount;
            }
        }
        if(leaderCount==1 && thinkerCount>=2 && balancerCount>=1 && uniqueRoles.size()>=3){
            System.out.println("Team is valid"+uniqueRoles.size()+":"+uniqueRoles.toString());
            isTeamValid=true;
        }
        return isTeamValid;
    }


    @Override
    public ArrayList<ArrayList<String>> createTeams() {
        int maxAttempts = 1000; // Max attempts per team formation
        int consecutiveFailures = 0;
        int maxConsecutiveFailures = 50; // Stop after 50 consecutive failures

        double minValue = avgSkillValue - 3;
        double maxValue = avgSkillValue + 3;

        while (hasEnoughPlayers()) {

            // Safety check - if we've failed too many times, stop
            if (consecutiveFailures >= maxConsecutiveFailures) {
                System.out.println("⚠️ Stopping: Too many consecutive failures (" +
                        consecutiveFailures + "). No valid teams can be formed.");
                break;
            }

            ArrayList<String> candidateTeam = tryFormTeam(maxAttempts);

            if (candidateTeam == null) {
                consecutiveFailures++;
                System.out.println("❌ Failed to form team (attempt " + consecutiveFailures +
                        "/" + maxConsecutiveFailures + ")");
                continue;
            }

            if(!validateTeam(candidateTeam)){
                consecutiveFailures++;
                continue;
            }

            // Validate the team
            if (!isGameCountValid(candidateTeam)) {
                consecutiveFailures++;
                continue;
            }

            int teamSkillSum = checkEachFormedTeamSkillSum(candidateTeam);

            // Check if a skill sum is within the acceptable range
            if (teamSkillSum >= minValue && teamSkillSum <= maxValue) {
                // Team is valid! Add it and remove players
                selectedTeamsInSecondFilter.add(new ArrayList<>(candidateTeam));
                removePlayers(candidateTeam);
                consecutiveFailures = 0; // Reset failure counter

                System.out.println("✅ Team " + selectedTeamsInSecondFilter.size() +
                        " formed (Skill Sum: " + teamSkillSum + ")");
            } else {
                consecutiveFailures++;
                System.out.println("⚠️ Team rejected (Skill Sum: " + teamSkillSum +
                        ", Range: " + minValue + "-" + maxValue + ")");
            }
        }
        printFinalStats();
        return  selectedTeamsInSecondFilter;


    }

//    private ArrayList<String> tryFormTeam(int maxAttempts) {
//        int balancerCount = teamPlayerCount - 3; // 1 leader + 2 thinkers = 3
//        int leaderCount = 1;
//        int thinkerCount = 2;
//
//        for (int attempt = 0; attempt < maxAttempts; attempt++) {
//            ArrayList<String> team = new ArrayList<>();
//            System.out.println("Wait..processing");
//
//            // Try to select 1 leader
//            if (remaining_all_leaders.isEmpty()) {
//                return null;
//            }
////            String leader = remaining_all_leaders.get(rand.nextInt(remaining_all_leaders.size()));
////            team.add(leader);
//
//            ArrayList<String> selectedLeaders = selectUniqueLeaders(leaderCount);
//            if (selectedLeaders == null || selectedLeaders.isEmpty()) {
//                continue;
//            }
//            team.add(String.valueOf(selectedLeaders));
//
//            // Try to select balancers
//            ArrayList<String> selectedBalancers = selectUniqueBalancers(balancerCount);
//            if (selectedBalancers == null || selectedBalancers.size() < balancerCount) {
//                continue; // Not enough balancers available
//            }
//            team.addAll(selectedBalancers);
//
//            // Try to select 2 thinkers
//            ArrayList<String> selectedThinkers = selectUniqueThinkers(thinkerCount);
//            if (selectedThinkers == null || selectedThinkers.size() < thinkerCount) {
//                continue; // Not enough thinkers available
//            }
//            team.addAll(selectedThinkers);
//
//            // Check if team size is correct
//            if (team.size() == teamPlayerCount) {
//                return team;
//            }
//        }
//
//        return null; // Failed to form team after max attempts
//    }

// In tryFormTeam method, fix the leader selection:

    private ArrayList<String> tryFormTeam(int maxAttempts) {
        int balancerCount = teamPlayerCount - 3;
        int leaderCount = 1;
        int thinkerCount = 2;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            ArrayList<String> team = new ArrayList<>();

            // ✅ FIX: Check if we have enough players before attempting
            if (remaining_all_leaders.size() < leaderCount ||
                    remaining_all_balancers.size() < balancerCount ||
                    remaining_all_thinkers.size() < thinkerCount) {
                return null; // Not enough players
            }

            // ✅ FIX: Proper leader selection (was adding a list as string)
            ArrayList<String> selectedLeaders = selectUniqueLeaders(leaderCount);
            if (selectedLeaders == null || selectedLeaders.isEmpty()) {
                continue;
            }
            team.addAll(selectedLeaders); // ✅ Use addAll, not add(String.valueOf())

            // Try to select balancers
            ArrayList<String> selectedBalancers = selectUniqueBalancers(balancerCount);
            if (selectedBalancers == null || selectedBalancers.size() < balancerCount) {
                // ✅ FIX: Return leaders to pool if team formation fails
                remaining_all_leaders.addAll(selectedLeaders);
                continue;
            }
            team.addAll(selectedBalancers);

            // Try to select 2 thinkers
            ArrayList<String> selectedThinkers = selectUniqueThinkers(thinkerCount);
            if (selectedThinkers == null || selectedThinkers.size() < thinkerCount) {
                // ✅ FIX: Return all selected players back to pools
                remaining_all_leaders.addAll(selectedLeaders);
                remaining_all_balancers.addAll(selectedBalancers);
                continue;
            }
            team.addAll(selectedThinkers);

            // Check if team size is correct
            if (team.size() == teamPlayerCount) {
                return team;
            }
        }

        return null;
    }


    @Override
    public ArrayList<String> selectUniqueLeaders(int count) {
        if (remaining_all_leaders.size() < count) {
            return null;
        }
        ArrayList<String> selected = new ArrayList<>();
        selected.add(remaining_all_leaders.remove(rand.nextInt(remaining_all_leaders.size())));
        return selected.size() == count ? selected : null;
    }
    @Override
    public ArrayList<String> selectUniqueBalancers(int count) {
        if (remaining_all_balancers.size() < count) {
            return null;
        }

        ArrayList<String> selected = new ArrayList<>();
        ArrayList<String> available = new ArrayList<>(remaining_all_balancers);

        for (int i = 0; i < count && !available.isEmpty(); i++) {
            int idx = rand.nextInt(available.size());
            selected.add(available.remove(idx));
        }

        return selected.size() == count ? selected : null;
    }
    @Override
    public ArrayList<String> selectUniqueThinkers(int count) {
        if (remaining_all_thinkers.size() < count) {
            return null;
        }

        ArrayList<String> selected = new ArrayList<>();
        ArrayList<String> available = new ArrayList<>(remaining_all_thinkers);

        for (int i = 0; i < count && !available.isEmpty(); i++) {
            int idx = rand.nextInt(available.size());
            selected.add(available.remove(idx));
        }

        return selected.size() == count ? selected : null;
    }

    private boolean hasEnoughPlayers() {
        int balancerCount = teamPlayerCount - 3;
        return remaining_all_leaders.size() >= 1 &&
                remaining_all_balancers.size() >= balancerCount &&
                remaining_all_thinkers.size() >= 2;
    }

    private void removePlayers(ArrayList<String> team) {
        for (String player : team) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            if (fields.length < 8) continue;

            String personalityType = fields[7].trim();

            switch (personalityType) {
                case "Leader" -> remaining_all_leaders.remove(player);
                case "Balanced" -> remaining_all_balancers.remove(player);
                case "Thinker" -> remaining_all_thinkers.remove(player);
            }
        }
    }

    @Override
    public boolean isGameCountValid(ArrayList<String> team) {
        HashMap<String, Integer> countMap = new HashMap<>();

        for (String player : team) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");

            if (fields.length > 3) {
                String thirdValue = fields[3].trim();
                countMap.put(thirdValue, countMap.getOrDefault(thirdValue, 0) + 1);
                if (countMap.get(thirdValue) > 2) {
                    return false; // More than 2 players from same game
                }
            }
        }
        return true;
    }
    public int checkEachFormedTeamSkillSum(ArrayList<String> team) {
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

    private void printFinalStats() {
        System.out.println("\n=== Final Remaining Players ===");
        System.out.println("Leaders: " + remaining_all_leaders.size());
        System.out.println("Balancers: " + remaining_all_balancers.size());
        System.out.println("Thinkers: " + remaining_all_thinkers.size());

        if (!remaining_all_leaders.isEmpty()) {
            System.out.println("\nRemaining Leaders: " + remaining_all_leaders);
        }
        if (!remaining_all_balancers.isEmpty()) {
            System.out.println("Remaining Balancers: " + remaining_all_balancers);
        }
        if (!remaining_all_thinkers.isEmpty()) {
            System.out.println("Remaining Thinkers: " + remaining_all_thinkers);
        }
        System.out.println(selectedTeamsInSecondFilter);
        System.out.println("\n✅ Total teams formed: " + selectedTeamsInSecondFilter.size());
    }
}
