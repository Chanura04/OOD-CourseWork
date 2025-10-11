package chooseTeams;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Player extends Person {
    // Each player personal details
    private int totalScore;
    private String preferredRole;
    private String personalityType;
    private int skillLevel;
    private String interestSport;

    //Player class constructor
    public Player(String id, String name, String email, String preferredRole, String personalityType, int skillLevel, String interestSport) {
        super(id, name, email);
        this.preferredRole = preferredRole;
        this.personalityType = personalityType;
        this.skillLevel = skillLevel;
        this.interestSport = interestSport;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void setEmail(String email) {
        this.email = email;
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
        return "Player ID: " + id + "\n" + "Player Name: " + name + "\n" + "Player Email: " + email + "\n" + "Player Preferred Role: " + preferredRole + "\n" + "Player Personality Type: " + personalityType + "\n" + "Player Skill Level: " + skillLevel + "\n" + "Player Interest Sport: " + interestSport;
    }

    // For get the personality type
    public int calculateTotalScore(int q1_ans, int q2_ans, int q3_ans, int q4_ans, int q5_ans){
        totalScore = (q1_ans + q2_ans + q3_ans + q4_ans + q5_ans) *4;
        setTotalScore(totalScore);
        return totalScore;
    }

    public String checkPersonalityType(){
        int score= getTotalScore();
        if(score>=90){
            return "Leader";
        }
        else if(score>=70){
            return "Balanced";
        }
        else if(score>=50){
            return "Thinker";
        }
        else{
            return "Nan";
        }
    }

    //Store player data into a csv file
    public void storePlayerData(){
        //save to csv file
        System.out.println("Player Data Stored Successfully");
    }


    public ArrayList<String> getPlayerData(){
        String filePath = "./data/participants_sample.csv";
        String line;
        Map<String, String> player = new HashMap<>();

        ArrayList<String> playerData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                player.put("id", values[0]);
                player.put("name", values[1]);
                player.put("email", values[2]);
                player.put("preferredGame", values[3]);
                player.put("skillLevel", values[4]);
                player.put("preferredRole", values[5]);
                player.put("personalityScore", values[6]);
                player.put("personalityType", values[7]);

                playerData.add(player.toString());

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return playerData;
    }

}
