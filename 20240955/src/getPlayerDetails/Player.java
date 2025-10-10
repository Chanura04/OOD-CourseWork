package getPlayerDetails;


public class Player {
    // Each player personal details
    public String id;
    public String name;
    public String email;
    public String preferredRole;
    public String personalityType;
    public int skillLevel;
    public String interestSport;

    //Player class constructor
    public Player(String id, String name, String email, String preferredRole, String personalityType, int skillLevel, String interestSport) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.preferredRole = preferredRole;
        this.personalityType = personalityType;
        this.skillLevel = skillLevel;
        this.interestSport = interestSport;
    }


}
