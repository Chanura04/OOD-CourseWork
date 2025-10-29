package chooseTeams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HandleRemainingPlayers {
    private ArrayList<String> remaining_all_leaders = new ArrayList<>();
    private ArrayList<String> remaining_all_balancers = new ArrayList<>();
    private ArrayList<String> remaining_all_thinkers = new ArrayList<>();
    private boolean hasTeam=false;
    private int teamPlayerCount;

    private Random rand = new Random();

    private ArrayList<String> selectedBalancers=new ArrayList<>();
    private ArrayList<String> selectedThinkers=new ArrayList<>();
    private ArrayList<String> selectedLeaders=new ArrayList<>();



    private ArrayList<String> newTeam = new ArrayList<>();
    public ArrayList<ArrayList<String>> newTeams = new ArrayList<>();

    public HandleRemainingPlayers(int teamPlayerCount) {
        this.teamPlayerCount = teamPlayerCount;

    }
    public ArrayList<ArrayList<String>> remainingTeamsCategorizeByPersonalityType(ArrayList<String> remainingPlayers, double avgSkillValue) {

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
        return  newTeams;

    }




    private void createNewTeams(double avgSkillValue) {
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

            // Validate the team
            if (!isGameCountValid(candidateTeam)) {
                consecutiveFailures++;
                continue;
            }

            int teamSkillSum = calculateSkillSum(candidateTeam);

            // Check if skill sum is within acceptable range
            if (teamSkillSum >= minValue && teamSkillSum <= maxValue) {
                // Team is valid! Add it and remove players
                newTeams.add(new ArrayList<>(candidateTeam));
                removePlayers(candidateTeam);
                consecutiveFailures = 0; // Reset failure counter

                System.out.println("✅ Team " + newTeams.size() +
                        " formed (Skill Sum: " + teamSkillSum + ")");
            } else {
                consecutiveFailures++;
                System.out.println("⚠️ Team rejected (Skill Sum: " + teamSkillSum +
                        ", Range: " + minValue + "-" + maxValue + ")");
            }
        }

        printFinalStats();
    }

    private ArrayList<String> tryFormTeam(int maxAttempts) {
        int balancerCount = teamPlayerCount - 3; // 1 leader + 2 thinkers = 3

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            ArrayList<String> team = new ArrayList<>();

            // Try to select 1 leader
            if (remaining_all_leaders.isEmpty()) {
                return null;
            }
//            String leader = remaining_all_leaders.get(rand.nextInt(remaining_all_leaders.size()));
//            team.add(leader);

            ArrayList<String> selectedLeaders = selectUniqueLeaders(1);
            if (selectedLeaders == null || selectedLeaders.isEmpty()) {
                continue;
            }
            team.add(String.valueOf(selectedLeaders));

            // Try to select balancers
            ArrayList<String> selectedBalancers = selectUniqueBalancers(balancerCount);
            if (selectedBalancers == null || selectedBalancers.size() < balancerCount) {
                continue; // Not enough balancers available
            }
            team.addAll(selectedBalancers);

            // Try to select 2 thinkers
            ArrayList<String> selectedThinkers = selectUniqueThinkers(2);
            if (selectedThinkers == null || selectedThinkers.size() < 2) {
                continue; // Not enough thinkers available
            }
            team.addAll(selectedThinkers);

            // Check if team size is correct
            if (team.size() == teamPlayerCount) {
                return team;
            }
        }

        return null; // Failed to form team after max attempts
    }

    private ArrayList<String> selectUniqueLeaders(int count) {
        if (remaining_all_leaders.size() < count) {
            return null;
        }
        ArrayList<String> selected = new ArrayList<>();
        selected.add(remaining_all_leaders.remove(rand.nextInt(remaining_all_leaders.size())));
        return selected.size() == count ? selected : null;
    }

    private ArrayList<String> selectUniqueBalancers(int count) {
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

    private ArrayList<String> selectUniqueThinkers(int count) {
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

    private boolean isGameCountValid(ArrayList<String> team) {
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

        System.out.println("\n✅ Total teams formed: " + newTeams.size());
    }
}
