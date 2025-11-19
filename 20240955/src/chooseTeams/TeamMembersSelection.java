package chooseTeams;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class TeamMembersSelection implements TeamSelection {
    private int teamPlayerCount;
    private final String csvFilePath;

    private final Object teamLock = new Object();

    private int secondFilterationFormedTeamsCount=0;
    //Loaded player data categorized by personality type and saved arraylists
    private final ArrayList<String> all_leaders=new ArrayList<>();
    private final ArrayList<String> all_balancers=new ArrayList<>();
    private final ArrayList<String> all_thinkers=new ArrayList<>();


    private ArrayList<String> cp_leaders;
    private ArrayList<String> cp_balancers;
    private ArrayList<String> cp_thinkers;

    //Store selected players for a team
    private final ArrayList<String> selectedBalancers=new ArrayList<>();
    private final ArrayList<String> selectedThinkers=new ArrayList<>();
    private final ArrayList<String> selectedLeaders=new ArrayList<>();

    //Store first filtered teams which team is satisfy the conditions
    private final ArrayList<ArrayList<String>> selectedTeamsInFirstFilter=new ArrayList<>();

    //Stored first filtered teams which team does not satisfy the conditions
    private final ArrayList<ArrayList<String>> remainingTeams=new ArrayList<>();

    //Concatenate first filtered teams and second filtered teams
    private final ArrayList<ArrayList<String>> finalTeamCombination=new ArrayList<>();

    private double average;


    //Store the rest of the players after formed all teams
    private ArrayList<String> rest_leaders=new ArrayList<>();
    private ArrayList<String> rest_balancers=new ArrayList<>();
    private ArrayList<String> rest_thinkers=new ArrayList<>();

    //Initial team combination for get average skill value
    private final ArrayList<ArrayList<String>> unfinalizedTeams = new ArrayList<>();

    private static final int MAX_ITERATIONS = 10000;
    private static final long TIMEOUT_MS = 30000; // 30 seconds timeout

    private double maximumSkillAverage;
    private double minimumSkillAverage;

    public TeamMembersSelection(int teamPlayerCount,String csvFilePath) {
        this.teamPlayerCount=teamPlayerCount;
        this.csvFilePath=csvFilePath;

    }

    public int getFinalTeamCombination() {
        return finalTeamCombination.size();
    }


    public double getMaximumSkillAverage() {
        return maximumSkillAverage;
    }
    public double getMinimumSkillAverage() {
        return minimumSkillAverage;
    }
    public int getTeamPlayerCount(){
        return teamPlayerCount;
    }
    public double getAverage(){
        return average;
    }


    public int getRest_leaders(){
        return rest_leaders.size();
    }

    public int getRest_balancers(){
        return rest_balancers.size();
    }
    public int getRest_thinkers(){
        return rest_thinkers.size();
    }




    public void categorizeByPersonalityType() {
        ParticipantDataLoader playerDataLoader = new ParticipantDataLoader();
        ArrayList<String> playerData = playerDataLoader.getPlayerData(csvFilePath);
        categorizeByPersonalityType(playerData);
    }

    public void categorizeByPersonalityType(ArrayList<String> playerData) {

        // Clear existing data
        all_leaders.clear();
        all_balancers.clear();
        all_thinkers.clear();

        for (int i = 1; i < playerData.size(); i++) {
            String raw = playerData.get(i).replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");
            String personalityType = fields[7].trim();
            switch (personalityType.strip()) {
                case "Leader" -> all_leaders.add(playerData.get(i));
                case "Balanced" -> all_balancers.add(playerData.get(i));
                case "Thinker" -> all_thinkers.add(playerData.get(i));
            }
        }

        // Create deep copies AFTER loading data
        cp_leaders = new ArrayList<>(all_leaders);
        cp_balancers = new ArrayList<>(all_balancers);
        cp_thinkers = new ArrayList<>(all_thinkers);

        System.out.println("\nLoaded - Leaders: " + cp_leaders.size() +
                ", Balancers: " + cp_balancers.size() +
                ", Thinkers: " + cp_thinkers.size());
        System.out.println("Loaded - Total players: " + playerData.size());
    }



    public ArrayList<String> selectUniqueLeaders(int count) {
        selectedLeaders.clear();
        if (cp_leaders.size() >= count) {
            Random rand = new Random();
            String leader = cp_leaders.get(rand.nextInt(cp_leaders.size()));
            selectedLeaders.add(leader);
        }
        return selectedLeaders;
    }

    public ArrayList<String> selectUniqueThinkers(int count) {
        selectedThinkers.clear();
        if (cp_thinkers.size() >= count) {
            Random rand = new Random();
            // Use a temporary list to avoid modification during selection
            ArrayList<String> available = new ArrayList<>(cp_thinkers);

            while (selectedThinkers.size() < count && !available.isEmpty()) {
                int idx = rand.nextInt(available.size());
                String thinker = available.remove(idx);
                selectedThinkers.add(thinker);
            }
        }
        return selectedThinkers;
    }

    public ArrayList<String> selectUniqueBalancers(int count) {
        selectedBalancers.clear();
        if (cp_balancers.size() >= count) {
            Random rand = new Random();
            // Use a temporary list to avoid modification during selection
            ArrayList<String> available = new ArrayList<>(cp_balancers);

            while (selectedBalancers.size() < count && !available.isEmpty()) {
                int idx = rand.nextInt(available.size());
                String balancer = available.remove(idx);
                selectedBalancers.add(balancer);
            }
        }
        return selectedBalancers;
    }



    //Check if the formed team include mandatory personality types
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



    public ArrayList<ArrayList<String>> createTeams() {
        int leaderCount = 1;
        int thinkerCount = 2;
        int balancerCount = getTeamPlayerCount() - 3;

        // Calculate maximum possible teams
        int maxPossibleTeams = Math.min(
                cp_leaders.size() / leaderCount,
                Math.min(cp_balancers.size() / balancerCount, cp_thinkers.size() / thinkerCount)
        );

        // Determine optimal thread count (based on CPU cores, but max 8)
        int threadCount = Math.min(Runtime.getRuntime().availableProcessors(), maxPossibleTeams);
        threadCount = Math.max(1, Math.min(threadCount, 8));

        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Thread> threads = new ArrayList<>();

        System.out.println("🔄 Starting team formation with " + threadCount + " threads...");

        // Create and start threads
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(
                    new TeamFormationTask(this, leaderCount, balancerCount, thinkerCount,
                            1000, latch, teamLock)
            );
            thread.setName("TeamFormation-" + (i + 1));
            thread.start();
            threads.add(thread);//use for check the monitor the thread
        }

        // Wait for all threads to complete
        try {
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (!completed) {
                System.err.println("⚠️ Team formation timeout - stopping threads");
                threads.forEach(Thread::interrupt);
            }
        } catch (InterruptedException e) {
            System.err.println("Team formation interrupted");
            threads.forEach(Thread::interrupt);
            Thread.currentThread().interrupt();
        }

        if (unfinalizedTeams.isEmpty()) {
            System.err.println("❌ No teams were formed! Check if you have enough players.");
            return unfinalizedTeams;
        }

        System.out.println("✅ Formed " + unfinalizedTeams.size() + " teams using parallel processing");

        formTeamsBySkillAverageValue();
        finalTeamsSelection();
        return unfinalizedTeams;

    }

    // helper methods (needed by TeamFormationTask)
    public boolean hasEnoughPlayersForTeam(int leaderCount, int balancerCount, int thinkerCount) {
        return cp_leaders.size() >= leaderCount &&
                cp_balancers.size() >= balancerCount &&
                cp_thinkers.size() >= thinkerCount;
    }
    public void addTeam(ArrayList<String> team) {
        unfinalizedTeams.add(team);
    }
    public void removeSelectedPlayers(ArrayList<String> leaders,
                                      ArrayList<String> balancers,
                                      ArrayList<String> thinkers) {
        cp_leaders.removeAll(leaders);
        cp_balancers.removeAll(balancers);
        cp_thinkers.removeAll(thinkers);
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
        int teamSize=unfinalizedTeams.size();
        for(int i=0;i<unfinalizedTeams.size();i++){
            ArrayList<String> team=unfinalizedTeams.get(i);
            int teamSkillSum=0;
            for(int j=0;j<team.size();j++){
                String raw = team.get(j).replace("[", "").replace("]", "").trim();
                String[] fields = raw.split(",");
                String getSkillValue = fields[4].trim();
                int skillValue=Integer.parseInt(getSkillValue);
                teamSkillSum+=skillValue;
            }
            totalSum +=teamSkillSum;

        }
        average = (double) totalSum / teamSize;


        for(int i=0;i<unfinalizedTeams.size();i++){
            ArrayList<String> team=unfinalizedTeams.get(i);
            int teamSkillSum=0;
            for(int j=0;j<team.size();j++){
                String raw = team.get(j).replace("[", "").replace("]", "").trim();
                String[] fields = raw.split(",");
                String getSkillValue = fields[4].trim();
                int skillValue=Integer.parseInt(getSkillValue);
                teamSkillSum+=skillValue;
            }

            maximumSkillAverage=average+3;
            minimumSkillAverage=average-3;


            if( teamSkillSum >= minimumSkillAverage && teamSkillSum <= maximumSkillAverage){
                selectedTeamsInFirstFilter.add(team);
            }else{
                remainingTeams.add(team);
            }
        }
        System.out.println("✅ Formed " + selectedTeamsInFirstFilter.size() + " teams after applying the filtering process.");

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

        combinedRemainingPlayers.addAll(remainingPlayers);


        HandleRemainingPlayers handleRemainingPlayers=new HandleRemainingPlayers(getTeamPlayerCount(),combinedRemainingPlayers, average,csvFilePath);
        System.out.println("\n\n");
        ArrayList<ArrayList<String>> handledRemainingTeams=handleRemainingPlayers.getCreatedTeams();
        secondFilterationFormedTeamsCount=handledRemainingTeams.size();
        rest_leaders=handleRemainingPlayers.getRemaining_all_leaders();
        rest_balancers=handleRemainingPlayers.getRemaining_all_balancers();
        rest_thinkers=handleRemainingPlayers.getRemaining_all_thinkers();

        finalTeamCombination.clear();
        finalTeamCombination.addAll(handledRemainingTeams);
        finalTeamCombination.addAll(selectedTeamsInFirstFilter);

//        printFinalStats();
//        writeFinalTeamsOnCsvFile();
//        writeRemainingPlayerInCsvFile();
    }




    public void writeFinalTeamsOnCsvFile(){
        int teamNumber=0;
        try (FileWriter writer = new FileWriter("files/possible_teams.csv")) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType,TeamNumber");
            writer.write("\n");

            for (ArrayList<String> group : finalTeamCombination) {
                teamNumber++;
                for (String row : group) {
                    String no=","+teamNumber;
                    row = row.replace("[", "").replace("]", no).trim();
                    writer.write(row);
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportFormedTeams(String filePath){
        Path sourcePath = Paths.get(filePath);

        //  Check if the file exists
        if (!Files.exists(sourcePath)) {
            System.out.println("❌ The file does not exist at: " + sourcePath);
            return;
        }

        //  Get the current working directory
        Path currentDir = Paths.get(System.getProperty("user.dir"));

        //  Set the destination path (same filename in current directory)
        Path destinationPath = currentDir.resolve(sourcePath.getFileName());
        System.out.println(sourcePath.getFileName());

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
//            System.out.println("✅ Teams exported to 'formed_teams.csv'");
            System.out.println("✅ Formed teams are successfully saved");


        } catch (IOException e) {
            System.out.println("⚠️ Error copying file: " + e.getMessage());
        }
    }

    public void writeRemainingPlayerInCsvFile(){

        ArrayList<String> restRemainingPlayers = new ArrayList<>();

        // Combine all remaining players
        restRemainingPlayers.addAll(rest_leaders);
        restRemainingPlayers.addAll(rest_balancers);
        restRemainingPlayers.addAll(rest_thinkers);

        try (FileWriter writer = new FileWriter("remaining_players.csv")) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");

            // Write each player as a new line
            for (String player : restRemainingPlayers) {
                player = player.replace("[", "").replace("]", "").trim();
                writer.write(player);
                writer.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
