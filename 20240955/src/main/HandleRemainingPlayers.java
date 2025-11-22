package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class HandleRemainingPlayers extends TeamMembersSelection {

    private final Object remainingLock = new Object();

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
        super(file_name);
        this.teamPlayerCount = teamPlayerCount;
        this.remainingPlayers = remainingPlayers;
        this.file_name=file_name;
        this.avgSkillValue=avgSkillValue;
        categorizeByPersonalityType();

    }


    public void returnPlayersToPool(ArrayList<String> team) {
        for (String player : team) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");
            if (fields.length < 8) continue;

            String personalityType = fields[7].trim();
            switch (personalityType) {
                case "Leader" -> remaining_all_leaders.add(player);
                case "Balanced" -> remaining_all_balancers.add(player);
                case "Thinker" -> remaining_all_thinkers.add(player);
            }
        }
    }

    public void addValidTeam(ArrayList<String> team) {
        selectedTeamsInSecondFilter.add(team);
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
        remaining_all_leaders.clear();
        remaining_all_balancers.clear();
        remaining_all_thinkers.clear();
        selectedTeamsInSecondFilter.clear();

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
            isTeamValid=true;
        }
        return isTeamValid;
    }


    @Override
    public ArrayList<ArrayList<String>> createTeams() {
        double minValue = avgSkillValue - 3;
        double maxValue = avgSkillValue + 3;

        // Determine thread count
        int threadCount = Math.min(Runtime.getRuntime().availableProcessors(), 4);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Thread> threads = new ArrayList<>();

        System.out.println("🔄 Processing remaining players with " + threadCount + " threads...");

        // Create and start threads
        for (int i = 0; i < threadCount; i++) {

            RemainingTeamFormationTask task= new RemainingTeamFormationTask(
                    this,
                    1000,
                    minValue,
                    maxValue,
                    latch,
                    remainingLock
            );


            Thread thread = new Thread(task);

            thread.setName("RemainingTeam-" + (i + 1));
            thread.start();
            threads.add(thread);
        }

        // Wait for all threads to complete
        try {
            boolean completed = latch.await(20, TimeUnit.SECONDS);
            if (!completed) {
                System.err.println("⚠️ Remaining team formation timeout - stopping threads");
                threads.forEach(Thread::interrupt);
            }
        } catch (InterruptedException e) {
            System.err.println("Remaining team formation interrupted");
            threads.forEach(Thread::interrupt);
            Thread.currentThread().interrupt();
        }

        System.out.println("✅ Formed " + selectedTeamsInSecondFilter.size() +
                " teams from remaining players");

        return selectedTeamsInSecondFilter;
    }


    public ArrayList<String> tryFormTeam(int maxAttempts) {
        int balancerCount = teamPlayerCount - 3;
        int leaderCount = 1;
        int thinkerCount = 2;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (!hasEnoughPlayers()) {
                return null;
            }

            ArrayList<String> team = new ArrayList<>();

            ArrayList<String> selectedLeaders = selectUniqueLeaders(leaderCount);
            if (selectedLeaders == null || selectedLeaders.isEmpty()) continue;

            ArrayList<String> selectedBalancers = selectUniqueBalancers(balancerCount);
            if (selectedBalancers == null || selectedBalancers.size() < balancerCount) {
                remaining_all_leaders.addAll(selectedLeaders);
                continue;
            }

            ArrayList<String> selectedThinkers = selectUniqueThinkers(thinkerCount);
            if (selectedThinkers == null || selectedThinkers.size() < thinkerCount) {
                remaining_all_leaders.addAll(selectedLeaders);
                remaining_all_balancers.addAll(selectedBalancers);
                continue;
            }

            team.addAll(selectedLeaders);
            team.addAll(selectedBalancers);
            team.addAll(selectedThinkers);

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

    public boolean hasEnoughPlayers() {
        int balancerCount = teamPlayerCount - 3;
        return remaining_all_leaders.size() >= 1 &&
                remaining_all_balancers.size() >= balancerCount &&
                remaining_all_thinkers.size() >= 2;
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
        System.out.println("\n=== Second Filtration  Teams Players ===");
        System.out.println("Leaders: " + remaining_all_leaders.size());
        System.out.println("Balancers: " + remaining_all_balancers.size());
        System.out.println("Thinkers: " + remaining_all_thinkers.size());
        System.out.println(selectedTeamsInSecondFilter);
        System.out.println("\n✅ Total teams formed: " + selectedTeamsInSecondFilter.size());
    }
}
