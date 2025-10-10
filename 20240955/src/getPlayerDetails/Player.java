package getPlayerDetails;


public class Player extends Person {
    // Each player personal details

    public String preferredRole;
    public String personalityType;
    public int skillLevel;
    public String interestSport;

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
    public String toString(){
        return "Player ID: " + id + "\n" + "Player Name: " + name + "\n" + "Player Email: " + email + "\n" + "Player Preferred Role: " + preferredRole + "\n" + "Player Personality Type: " + personalityType + "\n" + "Player Skill Level: " + skillLevel + "\n" + "Player Interest Sport: " + interestSport;
    }


}
