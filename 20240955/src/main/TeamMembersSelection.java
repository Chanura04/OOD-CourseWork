package main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.logging.*;

public class TeamMembersSelection implements TeamSelection {
    private int playersCountPerTeam;
    private final String uploadCsvFileName;

    private final Object teamLock = new Object();

    //Loaded player data categorized by personality type and saved arraylists
    private final ArrayList<String> all_leaders=new ArrayList<>();
    private final ArrayList<String> all_balancers=new ArrayList<>();
    private final ArrayList<String> all_thinkers=new ArrayList<>();


    private ArrayList<String> cp_leaders;
    private ArrayList<String> cp_balancers;
    private ArrayList<String> cp_thinkers;


    //Store first filtered teams which team is satisfy the conditions
    private final ArrayList<ArrayList<String>> selectedTeamsInFirstFilter=new ArrayList<>();
    private double average;

    //Initial team combination for get average skill value
    private final ArrayList<ArrayList<String>> unfinalizedTeams = new ArrayList<>();

    private double maximumSkillAverage;
    private double minimumSkillAverage;

    public TeamMembersSelection(String uploadCsvFileName) {
        this.uploadCsvFileName=uploadCsvFileName;
    }

    public int getTotalFinalTeamCombination() {
        return selectedTeamsInFirstFilter.size();
    }

    public double getMaximumSkillAverage() {
        return maximumSkillAverage;
    }
    public double getMinimumSkillAverage() {
        return minimumSkillAverage;
    }
    public int getTeamPlayerCount(){
        return playersCountPerTeam;
    }
    public double getAverage(){
        return average;
    }
    public int setTeamPlayerCount(int playersCountPerTeam){
        return this.playersCountPerTeam=playersCountPerTeam;
    }

    public int getRemainingLeadersCount(){
        return cp_leaders.size();
    }

    public int getRemainingBalancersCount(){
        return cp_balancers.size();
    }
    public int getRemainingThinkersCount(){
        return cp_thinkers.size();
    }

    public void defineTeamSize(Scanner input) {
        InputValidator inputValidator = new InputValidator();  //SQ 2.3 -Define team size
        if (uploadCsvFileName == null) {
            System.out.println("⚠️ Please import participant data first before generating teams.");
            return;
        }

        try {
            playersCountPerTeam = inputValidator.isValidInterInput(input,     // SQ 2.4 -Define team size
                    "\nEnter players per team (minimum 4): ", 4);

            System.out.println("\n🔄 Starting team formation with parallel processing...");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    //sq 2.2
    public  void generateTeams(Scanner input) {
        InputValidator inputValidator = new InputValidator();//sq 2.2.2
        try {
            long startTime = System.currentTimeMillis();
            categorizeByPersonalityType();//sq 2.2.3
            createTeams();//sq 2.2.6
            long endTime = System.currentTimeMillis();
            writeFinalTeamsOnCsvFile(); // sq 2.2.20

            File file = new File("C:\\Github Projects\\OOD-CourseWork\\20240955\\files\\possible_teams.csv");
            if (file.exists()) {
                ReviewGeneratedTeams vm = new ReviewGeneratedTeams(file);
                vm.setTeamPlayerCount(playersCountPerTeam);
                vm.viewFormedTeams();
            } else {
                System.out.println("⚠️ No teams file found. Please generate teams first.");
            }

            boolean isAcceptingFormedTeams=inputValidator.getValidResponseInput(input,"\nDo you accept these teams? (Y/N):","y","n");
            if(isAcceptingFormedTeams){
                exportFormedTeams("C:\\Github Projects\\OOD-CourseWork\\20240955\\files\\possible_teams.csv");//sq 2.2.21.1
                writeRemainingPlayerInCsvFile();//sq 2.2.21.2
                writeFormedTeamsStaticDetails();//sq 2.2.21.3

            }else{
                playersCountPerTeam=0;
                System.out.println("❌ Teams were not accepted. Export cancelled.");
                File file1 = new File("possible_teams.csv");
                if (file1.exists()) {
                    HandleDataCsvFiles handleDataCsvFiles = new HandleDataCsvFiles();//sq 2.2.22
                    handleDataCsvFiles.deleteCsvFile(file1); //sq 2.2.23
                }
            }
            System.out.println("\n⏱️ Team formation completed in " + (endTime - startTime) + "ms");

        } catch (Exception e) {
            System.out.println("⚠️ Error generating teams: " + e.getMessage());
        }
    }

    public void categorizeByPersonalityType() {
        ParticipantDataLoader playerDataLoader = new ParticipantDataLoader();//sq 2.2.4
        ArrayList<String> playerData = playerDataLoader.getPlayerData(uploadCsvFileName);//sq 2.2.5

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
        int total=cp_leaders.size()+cp_balancers.size()+cp_thinkers.size();

        System.out.println("\nLoaded - Leaders: " + cp_leaders.size() +
                ", Balancers: " + cp_balancers.size() +
                ", Thinkers: " + cp_thinkers.size());
        System.out.println("Loaded - Total players: " + total);
    }



    public ArrayList<String> selectUniqueLeaders(int count) {
        synchronized (teamLock) {
            ArrayList<String> selected = new ArrayList<>();

            if (cp_leaders.size() < count) {
                return selected;
            }
            Random rand = new Random();
            for (int i = 0; i < count; i++) {
                if (cp_leaders.isEmpty()) break;

                int idx = rand.nextInt(cp_leaders.size());
                String leader = cp_leaders.remove(idx);
                selected.add(leader);
            }
            return selected;
        }
    }


      //THREAD-SAFE: Select and REMOVE thinkers atomically
    public ArrayList<String> selectUniqueThinkers(int count) {
        synchronized (teamLock) {
            ArrayList<String> selected = new ArrayList<>();

            if (cp_thinkers.size() < count) {
                return selected;
            }
            Random rand = new Random();
            for (int i = 0; i < count; i++) {
                if (cp_thinkers.isEmpty()) break;

                int idx = rand.nextInt(cp_thinkers.size());
                String thinker = cp_thinkers.remove(idx);
                selected.add(thinker);
            }
            return selected;
        }
    }


    // THREAD-SAFE: Select and REMOVE balancers atomically
    public ArrayList<String> selectUniqueBalancers(int count) {
        synchronized (teamLock) {
            ArrayList<String> selected = new ArrayList<>();

            if (cp_balancers.size() < count) {
                return selected;
            }
            Random rand = new Random();
            for (int i = 0; i < count; i++) {
                if (cp_balancers.isEmpty()) break;

                int idx = rand.nextInt(cp_balancers.size());
                String balancer = cp_balancers.remove(idx);
                selected.add(balancer);
            }
            return selected;
        }
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


    //sq 2.2.6
    public ArrayList<ArrayList<String>> createTeams() {
        int leaderCount = 1;
        int thinkerCount = 2;
        int balancerCount = getTeamPlayerCount() - 3;

        // Calculate maximum possible teams
        int maxPossibleTeams = Math.min(
                cp_leaders.size() / leaderCount,
                Math.min(cp_balancers.size() / balancerCount, cp_thinkers.size() / thinkerCount)
        );

        // Determine optimal thread count (based on CPU cores, but max 20)
        int threadCount = Math.min(Runtime.getRuntime().availableProcessors(), maxPossibleTeams);
//        threadCount = Math.max(threadCount, 20);
        threadCount=500;


        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Thread> threads = new ArrayList<>();

        System.out.println("🔄 Starting team formation with " + threadCount + " threads...");

        // Create and start threads
        for (int i = 0; i < threadCount; i++) {

            // Create the task
            TeamFormationTask task = new TeamFormationTask( //sq 2.2.7
                    this,// Represent the TeamMemberSelection class object
                    leaderCount,
                    balancerCount,
                    thinkerCount,
                    5000,
                    latch,
                    teamLock
            );

            //  Give the takes to worker
            Thread thread = new Thread(task);

            // Give the thread a readable name
            thread.setName("TeamFormation-" + (i + 1));

            //  Start the thread
            thread.start();   //sq 2.2.8

            // Store for timeout control
            threads.add(thread);
        }

        // Wait for all threads to complete
        try {
            boolean completed = latch.await(100, TimeUnit.SECONDS);
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

        filterTeamsBySkillAverageValue(); //sq 2.2.19
        return unfinalizedTeams;

    }
    public boolean checkPlayerCount(int formedTeamSize){
        return playersCountPerTeam==formedTeamSize;
    }

    public void addTeam(ArrayList<String> team) {
        unfinalizedTeams.add(team);
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

    public void filterTeamsBySkillAverageValue(){
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
            maximumSkillAverage=average+6;
            minimumSkillAverage=average-6;

            if( teamSkillSum >= minimumSkillAverage && teamSkillSum <= maximumSkillAverage){
                selectedTeamsInFirstFilter.add(team);
            }else{
                returnPlayersToPool(team);
            }
        }
        System.out.println("✅ Formed " + selectedTeamsInFirstFilter.size() + " teams after applying the filtering process.");
    }
    public void returnPlayersToPool(ArrayList<String> team) {
        for (String player : team) {
            String raw = player.replace("[", "").replace("]", "").trim();
            String[] fields = raw.split(",");
            if (fields.length < 8) continue;

            String personalityType = fields[7].trim();
            switch (personalityType) {
                case "Leader" -> cp_leaders.add(player);
                case "Balanced" -> cp_balancers.add(player);
                case "Thinker" -> cp_thinkers.add(player);
            }
        }
    }

    public void writeFinalTeamsOnCsvFile(){
        int teamNumber=0;
        try (FileWriter writer = new FileWriter("C:\\Github Projects\\OOD-CourseWork\\20240955\\files\\possible_teams.csv")) {
            writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType,TeamNumber");
            writer.write("\n");

            for (ArrayList<String> group : selectedTeamsInFirstFilter) {
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
    public void writeFormedTeamsStaticDetails(){
        try (FileWriter writer = new FileWriter("C:\\Github Projects\\OOD-CourseWork\\20240955\\files\\staticData.csv")) {
            writer.write("PlayersPerTeam,TotalTeamCount,AverageSkillLevel,MinimumSkillLevel,MaximumSkillLevel,RemainingLeaderCount,RemainingBalancerCount,RemainingThinkerCount");
            writer.write("\n");
            writer.write(playersCountPerTeam + "," + selectedTeamsInFirstFilter.size()+','+average+','+minimumSkillAverage+','+maximumSkillAverage+','+getRemainingLeadersCount()+','+getRemainingBalancersCount()+','+getRemainingThinkersCount()+ "\n");
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
        restRemainingPlayers.addAll(cp_balancers);
        restRemainingPlayers.addAll(cp_leaders);
        restRemainingPlayers.addAll(cp_thinkers);

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

    public void returnPlayersToPool(ArrayList<String> leaders,
                                    ArrayList<String> balancers,
                                    ArrayList<String> thinkers) {
        synchronized (teamLock) {
            cp_leaders.addAll(leaders);
            cp_balancers.addAll(balancers);
            cp_thinkers.addAll(thinkers);
        }
    }


      // THREAD-SAFE: Check if enough players available
    public boolean hasEnoughPlayersForTeam(int leaderCount, int balancerCount, int thinkerCount) {
        synchronized (teamLock) {
            return cp_leaders.size() >= leaderCount &&
                    cp_balancers.size() >= balancerCount &&
                    cp_thinkers.size() >= thinkerCount;
        }
    }

}
