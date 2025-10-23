package chooseTeams;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        Scanner input = new Scanner(System.in);

        System.out.println("-----------Intelligent Team Formation System for University Gaming Club-----------\n");

        System.out.print("Enter your name: ");
        String name = input.nextLine();

        System.out.print("\nEnter your email: ");
        String email = input.nextLine();


        //-----------------Starting Personality Survey----------------
        System.out.println("\nFollowing personality survey each statement you can rate from 1 (Strongly Disagree) to 5 (Strongly Agree)\n");

        System.out.println("Q1) I enjoy taking the lead and guiding others during group activities.");
        System.out.print("Answer: ");
        int q1 = input.nextInt();

        System.out.println();
        System.out.println("Q2) I prefer analyzing situations and coming up with strategic solutions.");
        System.out.print("Answer: ");
        int q2 = input.nextInt();

        System.out.println();
        System.out.println("Q3) I work well with others and enjoy collaborative teamwork.");
        System.out.print("Answer: ");
        int q3 = input.nextInt();

        System.out.println();
        System.out.println("Q4) I am calm under pressure and can help maintain team morale.");
        System.out.print("Answer: ");
        int q4 = input.nextInt();

        System.out.println();
        System.out.println("Q5) I like making quick decisions and adapting in dynamic situations.");
        System.out.print("Answer: ");
        int q5 = input.nextInt();

        input.nextLine();
        System.out.println();
        System.out.print("Enter your interest game(eg:- Valorant, Dota, FIFA, Basketball, Badminton) :");
        String game = input.nextLine();


        System.out.println();
        System.out.print("Enter your game skill level: ");
        int gameSkillLevel = input.nextInt();
        input.nextLine();

        System.out.println();
        rolesDescription();
        System.out.print("Enter your game interesting role(eg:- Strategist,Attacker): ");
        String role = input.nextLine();



        Player player1 = new Player(name,email);
        player1.setSkillLevel(gameSkillLevel);
        player1.setPreferredRole(role);
        player1.setInterestSport(game);
        player1.calculateTotalScore(q1,q2,q3,q4,q5);

        System.out.println(player1);

        player1.storeNewPlayerData();



    }

    public static void  rolesDescription(){
        String description = """
                Role	        |    Description
                Strategist	    | Focuses on tactics and planning. Keeps the bigger picture in mind during gameplay.
                Attacker	    | Frontline player. Good reflexes, offensive tactics, quick execution.
                Defender	    | Protects and supports team stability. Good under pressure and team-focused.
                Supporter	    | Jack-of-all-trades. Adapts roles, ensures smooth coordination.
                Coordinator	    | Communication lead. Keeps the team informed and organized in real time.
                
                
                """;
        System.out.println(description);
    }

    public static void personalitySurvey_q1(){

    }




}
