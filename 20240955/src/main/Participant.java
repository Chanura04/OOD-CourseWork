package main;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import java.util.logging.Logger;


// Process survey data
public class Participant extends User {
    // Each player personal details
    private int totalScore;
    private String preferredRole;
    private String personalityType;
    private int skillLevel;
    private String interestSport;
    private String id;
    private static final Logger logger = Logger.getLogger(Participant.class.getName());
    private Participant player;
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
    public void setPersonalityType(String personalityType){
        this.personalityType = personalityType;
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

    public  void participantSurvey(Scanner input) {
        InputValidator inputValidator = new InputValidator();//sq 2.4
        if(isPlayerSurveyCompleted()){  //2.5
            System.out.println("⚠️ You have already completed the survey!!!");
            logger.warning("⚠️ You have already completed the survey!!!");
            return;
        }
        try {
            logger.info("Starting survey for " + getName());
            System.out.println("\n\n" + "=".repeat(80));
            System.out.println("                          PERSONALITY SURVEY");
            System.out.println("=".repeat(80));
            System.out.println("\n");
            System.out.println("Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)\n");


            int q1 = inputValidator.isValidInterInput(input,
                    "Q1) I enjoy taking the lead and guiding others during group activities.\n    Answer: ", 1, 5);//2.6
            int q2 = inputValidator.isValidInterInput(input,
                    "Q2) I prefer analyzing situations and coming up with strategic solutions.\n    Answer: ", 1, 5);//3.2
            int q3 = inputValidator.isValidInterInput(input,
                    "Q3) I work well with others and enjoy collaborative teamwork.\n    Answer: ", 1, 5);//4.2
            int q4 = inputValidator.isValidInterInput(input,
                    "Q4) I am calm under pressure and can help maintain team morale.\n    Answer: ", 1, 5);//5.2
            int q5 = inputValidator.isValidInterInput(input,
                    "Q5) I like making quick decisions and adapting in dynamic situations.\n    Answer: ", 1, 5);//6.2

           logger.info("Player " + getName() + " completed the personality survey");
            String game = inputValidator.isValidStringInput(input,
                    "\nEnter your game of interest (e.g., Valorant, Dota, FIFA, Basketball) or 'q' to cancel: ");//7.2

            if (game == null) {
                System.out.println("🔙 Survey cancelled.");
                return;
            }
            logger.info("Player " + getName() + " entered game of interest: " + game);

            interestSport = game.substring(0, 1).toUpperCase() + game.substring(1);

            skillLevel = inputValidator.isValidInterInput(input,
                    "Enter your skill level (1-10): ", 1, 10);//8.2
            logger.info("Player " + getName() + " entered skill level: " + skillLevel);
            rolesDescription();//9.2
            int roleNumber = inputValidator.isValidInterInput(input,
                    "Enter the number of your preferred role: ", 1, 5);//9.3

            preferredRole = switch (roleNumber) {
                case 1 -> "Strategist";
                case 2 -> "Attacker";
                case 3 -> "Defender";
                case 4 -> "Supporter";
                case 5 -> "Coordinator";
                default -> "Unknown";
            };
            logger.info("Player " + getName() + " entered preferred role: " + preferredRole);

            calculateTotalScore(q1, q2, q3, q4, q5);//10.2
            logger.info("Player " + getName() + " completed the survey and calculated total score: " + getTotalScore());
            boolean isSurveyValid = checkPersonalityType();//10.3

            if(!isSurveyValid){
                logger.warning("⚠️ Player " + getName() + " survey is invalid. Total score: " + getTotalScore());
            }

            //10.4
            boolean isConfirmed=inputValidator.getValidResponseInput(input,"\nConfirm your preferences (interest game, skill level, preferred role) ? (Y/N):","y","n");

            while(!isConfirmed){
                logger.info("Player " + getName() + " wants to change his/her preferences. Showing survey results.");
                int selectChanges = inputValidator.isValidInterInput(input,
                        "1) Interest game\n2) Skill level\n3) Preferred role\n4) Cancel\nChoice: ",1,4
                );//11.2
                logger.info("Player " + getName() + " selected option: " + selectChanges);
                try{
                    switch (selectChanges){
                        case 1://12.1
                            interestSport = inputValidator.isValidStringInput(input,
                                    "Enter your game of interest (e.g., Valorant, Dota, FIFA, Basketball) or 'q' to cancel: ");
                            break;
                        case 2://12.2
                            skillLevel = inputValidator.isValidInterInput(input,
                                    "Enter your skill level (1-10): ", 1, 10);
                            break;
                        case 3:
                            rolesDescription();//12.7
                            int newRoleNumber = inputValidator.isValidInterInput(input,
                                    "Enter the number of your preferred role: ", 1, 5);//12.8
                            preferredRole = switch (newRoleNumber) {
                                case 1 -> "Strategist";
                                case 2 -> "Attacker";
                                case 3 -> "Defender";
                                case 4 -> "Supporter";
                                case 5 -> "Coordinator";
                                default -> "Unknown";
                            };
                            break;
                        case 4:
                            System.out.println("⚠️ Changes cancelled.");
                    }
                    showSurveyResults();//13
                    isConfirmed=inputValidator.getValidResponseInput(input,"\nConfirm your preferences (interest game, skill level, preferred role) ? (Y/N):","y","n");
                }catch (Exception e){
                    System.out.println("⚠️ Error completing survey: " + e.getMessage());
                }
            }
            storeSurveyData();//14
            logger.info("Player " + getName() + " completed the survey and stored data.");

            if (isSurveyValid) {
                System.out.println("\n✅ Survey completed successfully!");
                System.out.println("📊 Your personality type has been determined based on your responses.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error completing survey: " + e.getMessage());
        }
    }

    private void showSurveyResults(){
        System.out.println("\n\n" + "=".repeat(80));
        System.out.println("                          PERSONALITY SURVEY RESULTS");
        System.out.println("=".repeat(80));
        System.out.println("\n");
        System.out.println("Total Score: " + getTotalScore());
        System.out.println("Personality Type: " + getPersonalityType());
        System.out.println("Preferred Role: " + getPreferredRole());
        System.out.println("Interest Sport: " + getInterestSport());
        System.out.println("Skill Level: " + getSkillLevel());
        System.out.println("Preferred Role: " + getPreferredRole());
        System.out.println("\n\n" + "=".repeat(80));
    }

    private  void rolesDescription() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                              GAME ROLES");
        System.out.println("=".repeat(80));
        System.out.println("\nRole          | Description");
        System.out.println("-".repeat(80));
        System.out.println("1) Strategist | Focuses on tactics and planning. Keeps the bigger picture in mind.");
        System.out.println("2) Attacker   | Frontline player. Good reflexes, offensive tactics, quick execution.");
        System.out.println("3) Defender   | Protects and supports team stability. Good under pressure.");
        System.out.println("4) Supporter  | Jack-of-all-trades. Adapts roles, ensures smooth coordination.");
        System.out.println("5) Coordinator| Communication lead. Keeps the team informed and organized.\n");
        System.out.println("=".repeat(80) + "\n");
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
        File playerDataFile = new File("C:\\Github Projects\\OOD-CourseWork\\20240955\\DataBase\\students_loop.csv");
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

    private boolean isPlayerSurveyCompleted(){
        String filePath = "C:\\Github Projects\\OOD-CourseWork\\20240955\\DataBase\\students_loop.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 2 && name.equals(values[1].trim())
                        && email.equals(values[2].trim()) && !values[3].equals("null")) {
                        return true;
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading file for " + player.getName());
            e.printStackTrace();
        }
        return false;
    }

    public void storeSurveyData(){
        String filePath = "C:\\Github Projects\\OOD-CourseWork\\20240955\\DataBase\\students_loop.csv";
        CountDownLatch latch = new CountDownLatch(1);

        // Create and start the thread with our separate task class
        SurveyProcessorTask surveyProcessorTask = new SurveyProcessorTask(this, filePath, latch);//15
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
       File playerDataFile = new File("C:\\Github Projects\\OOD-CourseWork\\20240955\\DataBase\\students_loop.csv");

        ArrayList<String> loadPlayerData=playerDataLoader.getPlayerData(playerDataFile);
       if(loadPlayerData.isEmpty()){
           return -1;
       }
       String lastPlayer= loadPlayerData.getLast();
       String[] playerData=lastPlayer.split(",");
       String id=playerData[0];
       String idValue=id.split("P")[1];
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
