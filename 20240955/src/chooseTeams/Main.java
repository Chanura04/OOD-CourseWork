package chooseTeams;

import javax.swing.text.View;
import java.io.File;
import java.util.Scanner;

public class Main {
    
    
    public static void main(String[] args) {
        int playersCount = 0;
        int rest_thinkers = 0;
        int rest_leaders = 0;
        int rest_balancers=0;
        double  avgSkillValue=0;
        double  minAvg=0;
        double  maxAvg=0;



    
        Scanner input = new Scanner(System.in);
        System.out.println("-----------Intelligent Team Formation System for University Gaming Club-----------\n");

        while(true){
            display();
            System.out.print("Choice: ");
            int choice = input.nextInt();


            switch (choice){
                case 1:

                    System.out.print("Enter your name: ");
                    String name = input.nextLine();
                    input.nextLine();

                    System.out.print("\nEnter your email: ");
                    String email = input.nextLine();


                    //-----------------Starting Personality Survey----------------
                    System.out.println("\nFollowing personality survey each statement you can rate from 1 (Strongly Disagree) to 5 (Strongly Agree)\n");

                    System.out.println("Q1) I enjoy taking the lead and guiding others during group activities.");
                    System.out.print("Answer: ");
                    int q1 = input.nextInt();
                    if(q1>5){
                        System.out.println("Please enter a number between 1 and 5");
                        break;
                    }

                    System.out.println();
                    System.out.println("Q2) I prefer analyzing situations and coming up with strategic solutions.");
                    System.out.print("Answer: ");
                    int q2 = input.nextInt();
                    if(q2>5){
                        System.out.println("Please enter a number between 1 and 5");
                        break;
                    }

                    System.out.println();
                    System.out.println("Q3) I work well with others and enjoy collaborative teamwork.");
                    System.out.print("Answer: ");
                    int q3 = input.nextInt();
                    if(q3>5){
                        System.out.println("Please enter a number between 1 and 5");
                        break;
                    }

                    System.out.println();
                    System.out.println("Q4) I am calm under pressure and can help maintain team morale.");
                    System.out.print("Answer: ");
                    int q4 = input.nextInt();
                    if(q4>5){
                        System.out.println("Please enter a number between 1 and 5");
                        break;
                    }

                    System.out.println();
                    System.out.println("Q5) I like making quick decisions and adapting in dynamic situations.");
                    System.out.print("Answer: ");
                    int q5 = input.nextInt();
                    if(q5>5){
                        System.out.println("Please enter a number between 1 and 5");
                        break;
                    }



                    input.nextLine();
                    System.out.println();
                    System.out.print("Enter your interest game(eg:- Valorant, Dota, FIFA, Basketball, Badminton) :");
                    String game = input.nextLine();
                    String capitalizedGameName = game.substring(0, 1).toUpperCase() + game.substring(1);


                    System.out.println();
                    System.out.print("Enter your game skill level(1 to 10): ");
                    int gameSkillLevel = input.nextInt();
                    input.nextLine();

                    System.out.println();
                    rolesDescription();
                    System.out.print("Enter your game interesting role belongs number: ");
                    int roleNumber = input.nextInt();
                    String role = "";
                    if(roleNumber==1){
                        role = "Strategist";
                    }else if(roleNumber==2){
                        role = "Attacker";
                    }else if(roleNumber==3){
                        role = "Defender";
                    }else if(roleNumber==4){
                        role = "Supporter";
                    }else if(roleNumber==5){
                        role = "Coordinator";
                    }



                    Player player1 = new Player(name,email);
                    player1.setSkillLevel(gameSkillLevel);
                    player1.setPreferredRole(role);
                    player1.setInterestSport(capitalizedGameName);
                    player1.calculateTotalScore(q1,q2,q3,q4,q5);

                    System.out.println(player1);

                    player1.storeNewPlayerData();
                    break;
                case 2:
                    System.out.print("Enter the number of players in your team( Min players per team is 4): ");
                    int teamPlayerCount = input.nextInt();

                    if( teamPlayerCount<4){
                        System.out.println("Invalid input. Please try again.");
                        break;
                    }


                    playersCount=teamPlayerCount;
                    TeamMembersSelection teamMembersSelection=new TeamMembersSelection(teamPlayerCount);
                    teamMembersSelection.getCreatedTeams();
                    avgSkillValue=teamMembersSelection.getAverage();

                    minAvg =teamMembersSelection.getMinimumSkillAverage();
                    maxAvg= teamMembersSelection.getMaximumSkillAverage();


                    rest_thinkers=teamMembersSelection.getRest_thinkers();
                    rest_leaders=teamMembersSelection.getRest_leaders();
                    rest_balancers=teamMembersSelection.getRest_balancers();

                    break;

                case 3:
                    File file = new File("formed_teams.csv");

                    if(playersCount==0) {
                        System.out.print("Please create team combinations!!!");
                        break;

                    }

                    if (file.exists()) {
                    ViewTeams vm=new ViewTeams();
                    vm.setTeamPlayerCount(playersCount);
                    vm.viewFormedTeams();
                    }
                    else{
                        System.out.println("Please create team combinations!!!");
                        break;
                    }
                    break;

                case 4:
                    if(playersCount==0) {
                        System.out.println("Please create team combinations!!!");
                        break;
                    }
                    System.out.println("Remaining Leaders count is: "+rest_leaders);
                    System.out.println("Remaining Balancers count is: "+rest_balancers);
                    System.out.println("Rest thinkers count is: "+rest_thinkers);


                    ViewRemainingPlayers viewRemainingPlayers=new ViewRemainingPlayers();
                    viewRemainingPlayers.viewRemainingPlayerInCsvFile();
                    break;

                case 5:
                    if(playersCount==0) {
                        System.out.println("Please create team combinations!!!");
                        break;
                    }


                    System.out.printf("Average Skill Value: %.2f", avgSkillValue);
                    System.out.println();
                    System.out.printf("Team skill range %.2f - %.2f",minAvg,maxAvg);

                    break;

                case 6:
                    System.out.println("Exiting...");
                    input.close();
                    System.exit(0);
                    break;

                default:


            }

        }




    }

    public static void  rolesDescription(){
        String description = """
                    Role	        |    Description
                1) Strategist	    | Focuses on tactics and planning. Keeps the bigger picture in mind during gameplay.
                2)  Attacker	    | Frontline player. Good reflexes, offensive tactics, quick execution.
                3)  Defender	    | Protects and supports team stability. Good under pressure and team-focused.
                4)  Supporter	    | Jack-of-all-trades. Adapts roles, ensures smooth coordination.
                5)  Coordinator	    | Communication lead. Keeps the team informed and organized in real time.
                
                
                """;
        System.out.println(description);
    }

    public static void display(){

        String menu= """
                
                1) Add Players
                2) Create Teams
                3) View Teams
                4) View Remaining Players
                5) View Skill Average Threshold
                6) Exit
                
                """;
        System.out.println(menu);

    }
  }
