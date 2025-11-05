package chooseTeams;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class TeamMembersSelection implements TeamSelection {
    private int teamPlayerCount;
    private final String csvFilePath;

    //Loaded player data categorized by personality type and saved arraylists
    private final ArrayList<String> all_leaders=new ArrayList<>();
    private final ArrayList<String> all_balancers=new ArrayList<>();
    private final ArrayList<String> all_thinkers=new ArrayList<>();


//    private final ArrayList<String> cp_leaders=all_leaders;
//    private final ArrayList<String> cp_balancers=all_balancers;
//    private final ArrayList<String> cp_thinkers=all_thinkers;
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
    private boolean usingCustomPlayerList = false;
    public TeamMembersSelection(int teamPlayerCount,String csvFilePath) {
        this.teamPlayerCount=teamPlayerCount;
        this.csvFilePath=csvFilePath;

    }

    public ArrayList<ArrayList<String>> getFinalTeamCombination() {
        return finalTeamCombination;
    }

    public void setMaximumSkillAverage(double maximumSkillAverage) {
        this.maximumSkillAverage = maximumSkillAverage;
    }
    public void setMinimumSkillAverage(double minimumSkillAverage) {
        this.minimumSkillAverage = minimumSkillAverage;
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
    public  void setAverage(double average){
        this.average=average;
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
        PlayerDataLoader playerDataLoader = new PlayerDataLoader();
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

        System.out.println("Loaded - Leaders: " + cp_leaders.size() +
                ", Balancers: " + cp_balancers.size() +
                ", Thinkers: " + cp_thinkers.size());
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
//            System.out.println("Team is valid"+uniqueRoles.size()+":"+uniqueRoles.toString());
            isTeamValid=true;
        }
        return isTeamValid;
    }



    public ArrayList<ArrayList<String>> createTeams() {
//        categorizeByPersonalityType();

        int leaderCount = 1;
        int thinkerCount = 2;
        int balancerCount = getTeamPlayerCount() - 3;

//        System.out.println("Player count per team: " + getTeamPlayerCount());
//        System.out.println("Required per team - Leaders: " + leaderCount +
//                ", Balancers: " + balancerCount +
//                ", Thinkers: " + thinkerCount);

        // Add timeout and iteration limit
        long startTime = System.currentTimeMillis();
        int iterations = 0;
        int consecutiveFailures = 0;
        final int MAX_CONSECUTIVE_FAILURES = 100;

        // Keep forming teams while there are enough people
        while (cp_thinkers.size() >= thinkerCount &&
                cp_balancers.size() >= balancerCount &&
                cp_leaders.size() >= leaderCount) {

            // Safety checks
            iterations++;
            if (iterations > MAX_ITERATIONS) {
//                System.err.println("⚠️ Reached maximum iterations (" + MAX_ITERATIONS + "). Stopping team formation.");
                break;
            }

            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed > TIMEOUT_MS) {
//                System.err.println("⚠️ Timeout reached (" + (TIMEOUT_MS/1000) + "s). Stopping team formation.");
                break;
            }

            if (consecutiveFailures > MAX_CONSECUTIVE_FAILURES) {
//                System.err.println("⚠️ Too many consecutive failures (" + consecutiveFailures + "). Stopping team formation.");
                break;
            }

            // Clear per-selection lists
            selectedLeaders.clear();
            selectedThinkers.clear();
            selectedBalancers.clear();

            //  Check if selection was successful
            ArrayList<String> leaders = selectUniqueLeaders(leaderCount);
            ArrayList<String> balancers = selectUniqueBalancers(balancerCount);
            ArrayList<String> thinkers = selectUniqueThinkers(thinkerCount);

            if (leaders.isEmpty() || balancers.isEmpty() || thinkers.isEmpty()) {
                consecutiveFailures++;
//                System.err.println("⚠️ Failed to select enough players (attempt " + consecutiveFailures + ")");
                continue;
            }

            // Create a proper team list
            ArrayList<String> team = new ArrayList<>();
            team.addAll(selectedLeaders);
            team.addAll(selectedBalancers);
            team.addAll(selectedThinkers);

            // Check team has required personality types
            if (!validateTeam(team)) {
                consecutiveFailures++;
                continue;
            }

            // Ensure it is exactly user input members count before storing
            if (team.size() == getTeamPlayerCount()) {
                if (!isGameCountValid(team)) {
                    consecutiveFailures++;
                    continue;
                }
                unfinalizedTeams.add(team);
                consecutiveFailures = 0; // Reset on success

                // Remove selected members from the pools
                cp_leaders.removeAll(selectedLeaders);
                cp_balancers.removeAll(selectedBalancers);
                cp_thinkers.removeAll(selectedThinkers);


            } else {
                consecutiveFailures++;
                System.err.println("⚠️ Unexpected team size: " + team.size());
            }
        }
        if (unfinalizedTeams.isEmpty()) {
            System.err.println("❌ No teams were formed! Check if you have enough players.");
            return unfinalizedTeams;
        }

        formTeamsBySkillAverageValue();
        finalTeamsSelection();
        return unfinalizedTeams;
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
//            System.out.println("Team "+(i+1)+" skill sum:"+teamSkillSum);
            totalSum +=teamSkillSum;

        }
        average = (double) totalSum / teamSize;

        setAverage(average);


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

            double maxValue=average+3;
            double minValue=average-3;
            setMaximumSkillAverage(maxValue);
            setMinimumSkillAverage(minValue);

//            System.out.println("minimum"+minValue+"maximum"+maxValue);

            if( teamSkillSum >= minValue && teamSkillSum <= maxValue){
                selectedTeamsInFirstFilter.add(team);
            }else{
                remainingTeams.add(team);
            }
        }

//        System.out.println("Selected Teams In First Filter:"+selectedTeamsInFirstFilter);
//        System.out.println("selectedTeamsInFirstFilter.size():"+selectedTeamsInFirstFilter.size());
//        System.out.println("Remaining Teams:"+remainingTeams);
//        System.out.println("remainingTeams.size():"+remainingTeams.size());

    }
    int count=0;
    public void finalTeamsSelection(){
        ArrayList<String> remainingPlayers = new ArrayList<>();
        ArrayList<String> combinedRemainingPlayers = new ArrayList<>();


        remainingPlayers.addAll(cp_leaders);
        remainingPlayers.addAll(cp_balancers);
        remainingPlayers.addAll(cp_thinkers);

        for (ArrayList<String> team : remainingTeams) {
            combinedRemainingPlayers.addAll(team);
        }

        //combine remaining players for next filtering process
        combinedRemainingPlayers.addAll(remainingPlayers);


        HandleRemainingPlayers handleRemainingPlayers=new HandleRemainingPlayers(getTeamPlayerCount(),combinedRemainingPlayers, average,csvFilePath);
        System.out.println("\n\n");
        ArrayList<ArrayList<String>> handledRemainingTeams=handleRemainingPlayers.getCreatedTeams();
        count=handledRemainingTeams.size();
        rest_leaders=handleRemainingPlayers.getRemaining_all_leaders();
        rest_balancers=handleRemainingPlayers.getRemaining_all_balancers();
        rest_thinkers=handleRemainingPlayers.getRemaining_all_thinkers();


        finalTeamCombination.addAll(handledRemainingTeams);
        finalTeamCombination.addAll(selectedTeamsInFirstFilter);

        printFinalStats();
        writeFinalTeamsOnCsvFile();
        writeRemainingPlayerInCsvFile();
    }
    private void printFinalStats() {
        System.out.println("\n********** Final  results **********");
        System.out.println("Leaders: " + rest_leaders.size());
        System.out.println("Balancers: " + rest_balancers.size());
        System.out.println("Thinkers: " + rest_thinkers.size());

        System.out.println("First filter selected teams: " + selectedTeamsInFirstFilter.size());
        System.out.println("Second filter remaining teams: " + count);

        System.out.println("✅ Total teams formed: " + finalTeamCombination.size());
        System.out.println();
    }

    public void randomizeTeamCombinationAgain() {
        System.out.println("\n===================================== Randomizing again =====================================");


        ArrayList<String> convertedList = new ArrayList<>();

        // Flatten all teams into a single list
        for (ArrayList<String> team : finalTeamCombination) {
            for (String player : team) {
                convertedList.add(player);
            }
        }

        // Shuffle the players
        Collections.shuffle(convertedList);



        // Clear previous results
        unfinalizedTeams.clear();
        selectedTeamsInFirstFilter.clear();
        remainingTeams.clear();
        finalTeamCombination.clear();

        rest_leaders.clear();
        rest_balancers.clear();
        rest_thinkers.clear();

        average=0;
        count=0;

        maximumSkillAverage=0;
        minimumSkillAverage=0;

        all_leaders.clear();
        all_balancers.clear();
        all_thinkers.clear();

        cp_leaders.clear();
        cp_balancers.clear();
        cp_thinkers.clear();

        selectedLeaders.clear();
        selectedBalancers.clear();
        selectedThinkers.clear();
        // Categorize with shuffled list
        categorizeByPersonalityType(convertedList);

        // Reuse the same createTeams() method!
        createTeams();
    }


    public void writeFinalTeamsOnCsvFile(){
        int teamNumber=0;
        try (FileWriter writer = new FileWriter("formed_teams.csv")) {
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

            System.out.println("✅ Exported formed_teams.csv successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
