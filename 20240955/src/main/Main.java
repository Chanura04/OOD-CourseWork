package main;

import java.util.InputMismatchException;
import java.util.Scanner;



public class Main {
    static String currentUserName;
    static String currentUserEmail;
    static int currentUserStoredRawNumber;



    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("=".repeat(80));
        System.out.println("\n      Intelligent Team Formation System for University Gaming Club\n");
        System.out.println("=".repeat(80));
        System.out.println("\n");

        mainMenu(input);
        input.close();
    }


    private static void mainMenu(Scanner input) {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("                              MAIN MENU");
                System.out.println("=".repeat(80));
                System.out.println("\n 1) Login as Organizer");
                System.out.println(" 2) Login as Participant");
                System.out.println(" 3) Register New Participant");
                System.out.println(" 4) Register New Organizer");
                System.out.println(" 5) Exit\n");
                System.out.println("=".repeat(80));
                System.out.println("\n");

                int choice = getValidIntegerInput(input, "Enter your choice: ", 1, 5);

                switch (choice) {
                    case 1:
                        Login organizerLogin = new Login();
                        boolean isOrganizerLogged=organizerLogin.organizerLogin(input);
                        if (isOrganizerLogged) {
                            Dashboard dashboard=new Dashboard();
                            dashboard.organizerDashboard(input);
                        }
                        break;
                    case 2:
                        Login participantLogin = new Login();
                        boolean isLogged=participantLogin.participantLogin(input);
                        currentUserName=participantLogin.getCurrentUserName();
                        currentUserEmail=participantLogin.getCurrentUserEmail();
                        currentUserStoredRawNumber=participantLogin.getCurrentUserStoredRawNumber();

                        if (isLogged) {
                            Dashboard dashboard=new Dashboard();//6
                            dashboard.setCurrentUserName(currentUserName);//6.1
                            dashboard.setCurrentUserEmail(currentUserEmail);//6.2
                            dashboard.setCurrentUserStoredRawNumber(currentUserStoredRawNumber);//6.3
                            dashboard.participantDashboard(input);//6.4
                        }
                        break;
                    case 3:
                        Registration registration=new Registration();//2.1
                        registration.registerNewParticipant(input);//2.2
                        break;
                    case 4:
                        Registration registration1=new Registration();//2.1
                        registration1.registerNewOrganizer(input);//2.2
                        break;
                    case 5:
                        System.out.println("\n✅ Thank you for using the Team Formation System. Goodbye!");
                        return;
                }
            } catch (Exception e) {
                System.out.println("⚠️ An unexpected error occurred: " + e.getMessage());
                System.out.println("⚠️ Returning to main menu...");
                input.nextLine();
            }
        }
    }

    private static int getValidIntegerInput(Scanner input, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = input.nextInt();
                input.nextLine();
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("Please enter a number between %d and %d.\n", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.println("⚠️ Invalid input. Please enter a valid number.");
                input.nextLine();
            }
        }
    }
}