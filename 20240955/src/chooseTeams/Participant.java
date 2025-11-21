package chooseTeams;


import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// Process survey data
public class Participant extends User {
    // Each player personal details
    private int totalScore;
    private String preferredRole;
    private String personalityType;
    private int skillLevel;
    private String interestSport;
    private String id;
    private int playerDataRowNumber;

    //Player class constructor
    public Participant(String name, String email) {
        super( name, email);
        setId(newPlayerId());

    }



    public void setId(String id) {
        this.id = id;
    }
    public String getId(){
        return id;
    }


    public String getPreferredRole(){
        return preferredRole;
    }
    public String getPersonalityType(){
        return personalityType;
    }
    public int getSkillLevel(){
        return skillLevel;
    }
    public String getInterestSport(){
        return interestSport;
    }
    public void setPreferredRole(String preferredRole){
        this.preferredRole = preferredRole;
    }
    public void setPersonalityType(String personalityType){
        this.personalityType = personalityType;
    }
    public void setSkillLevel(int skillLevel){
        this.skillLevel = skillLevel;
    }
    public void setInterestSport(String interestSport){
        this.interestSport = interestSport;
    }
    public int getTotalScore(){
        return totalScore;
    }
    public void setTotalScore(int totalScore){
        this.totalScore = totalScore;
    }

    public String toString(){
        return "Player ID: " + getId() + "\n" + "Player Name: " + getName() + "\n" + "Player Email: " + getEmail() + "\n" + "Player Preferred Role: " + getPreferredRole() + "\n" + "Player Personality Type: " + getPersonalityType() + "\n" + "Player Skill Level: " + skillLevel + "\n" + "Player Interest Sport: " + interestSport;
    }

    // For get the personality type
    public void calculateTotalScore(int q1_ans, int q2_ans, int q3_ans, int q4_ans, int q5_ans){
        totalScore = (q1_ans + q2_ans + q3_ans + q4_ans + q5_ans) *4;
        setTotalScore(totalScore);
    }

    public boolean checkPersonalityType(){
        int score= getTotalScore();
        if(score>=90){
            setPersonalityType("Leader");
        }
        else if(score>=70){
            setPersonalityType("Balanced");
        }
        else if(score>=50){
            setPersonalityType("Thinker");
        }
        else{
            return false;
        }
        return true;
    }

    //Store player data into a csv file
    public void storeRegisteredPlayerData(){
        File playerDataFile = new File("DataBase/students_loop.csv");
        //save to csv file
            try (FileWriter writer = new FileWriter(playerDataFile, true)) {
                String[] data={
                        getId(),getName(),getEmail(),"null","null","null","null","null"
                };
                writer.append(String.join(",", data));
                writer.append("\n");


            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void storeSurveyData(){

        String filePath = "DataBase/students_loop.csv";
        CountDownLatch latch = new CountDownLatch(1);

        // Create and start the thread with our separate task class
        SurveyProcessorTask surveyProcessorTask = new SurveyProcessorTask(this, filePath, latch);
        Thread surveyThread = new Thread(surveyProcessorTask);
        surveyThread.setName("Survey-" + getName());
        surveyThread.start();

        try {
            // Wait for the thread to complete (max 10 seconds)
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Survey processing interrupted for " + getName());
            Thread.currentThread().interrupt();
        }

    }


    public int getStoredLastId(){
       ParticipantDataLoader playerDataLoader=new ParticipantDataLoader();
       File playerDataFile = new File("DataBase/students_loop.csv");

        ArrayList<String> loadPlayerData=playerDataLoader.getPlayerData(playerDataFile);
       if(loadPlayerData.isEmpty()){
           return -1;
       }
       String lastPlayer= loadPlayerData.getLast();
       String[] playerData=lastPlayer.split(",");
       String id=playerData[0];
       String idValue=id.split("P")[1];
//       System.out.println("Last"+idValue);

       return Integer.parseInt(idValue);
    }

    public String newPlayerId(){
        int previousId=getStoredLastId();
        if(previousId==-1){
            setId("P1");
        }

        setId("P"+(previousId + 1));
        return "P"+(previousId + 1);
    }



}
