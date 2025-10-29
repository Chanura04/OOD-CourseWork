package chooseTeams;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends Person {
    // Each player personal details
    private int totalScore;
    private String preferredRole;
    private String personalityType;
    private int skillLevel;
    private String interestSport;
    private String id;

    //Player class constructor
    public Player( String name, String email) {
        super( name, email);

        setId(newPlayerId());
    }


    public void setId(String id) {
        this.id = id;
    }
    public String getId(){
        return id;
    }

//    @Override
//    public void setName(String name) {
//        this.name = name;
//    }
//    @Override
//    public void setEmail(String email) {
//        this.email = email;
//    }

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
    public void storeNewPlayerData(){
        //save to csv file
        if(checkPersonalityType()){
            try (FileWriter writer = new FileWriter("students_loop.csv", true)) {
                String[] data={
                        getId(),getName(),getEmail(),getInterestSport(), String.valueOf(getSkillLevel()),getPreferredRole(), String.valueOf(getTotalScore()),getPersonalityType()
                };
                writer.append(String.join(",", data));
                writer.append("\n");

                System.out.printf("%s successfully added!",getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Your Personality Score is very low!!!. please try again!");
        }


    }

    public int getStoredLastId(){
       PlayerDataLoader playerDataLoader=new PlayerDataLoader();
       ArrayList<String> loadPlayerData=playerDataLoader.getPlayerData();
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

//    @Override
//    public String toString(){
//        return "Player ID: " + id+"\n" + "Player Name: " + name+"\n"+"Email: "+email;
//
//    }


}
