package main;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputValidator {

    public int isValidInterInput(Scanner input, String prompt, int min, int max) {
        while (true) {
            try {
//                System.out.print(prompt);
                display(prompt);
                int value = input.nextInt();
                input.nextLine();
                if (value >= min && value <= max) {
                    return value;
                } else {
                    display("Please enter a number between %d and %d.\n", min, max);
                }
            } catch (InputMismatchException e) {
                display("⚠️ Invalid input. Please enter a valid number.");
                input.nextLine();
            }
        }
    }
    public boolean getValidResponseInput(Scanner input, String prompt, String y, String n) {
        while (true) {
            try {
                display(prompt);
                String value = input.nextLine().trim().toLowerCase();
                if (value.equals(y) || value.equals(n)) {
                    if(value.equals("y")){
                        return true;
                    }else{
                        return false;
                    }
                }else {
                    display("Please enter a valid response: y or n.\n ", y, n);

                }
            } catch (InputMismatchException e) {
                display("⚠️ Invalid input. Please enter a valid number: ");
                input.nextLine();
            }
        }
    }

    public  String isValidStringInput(Scanner input, String prompt) {
        while (true) {
            try {
                display(prompt);
                String value = input.nextLine().trim();

                if (value.equalsIgnoreCase("q")) {
                    return null;
                }

                // Allow only letters and spaces AND NUMBERS
                if (!value.matches("[a-zA-Z0-9 @._\\\\:-]+")) {
                    display("⚠️ Error: input must contain only letters!");
                    return isValidStringInput(input, prompt);
                }

                if (!value.isEmpty()) {
                    return value;
                }
                display("⚠️ Input cannot be empty. Please try again or enter 'q' to cancel.");


            } catch (Exception e) {
                System.out.println("⚠️ Error reading input: " + e.getMessage());
                input.nextLine(); // Clear buffer
            }
        }
    }
    public  int isValidInterInput(Scanner input, String prompt, int min) {
        while (true) {
            try {
//                System.out.print(prompt);
                display(prompt);
                int value = input.nextInt();
                input.nextLine();
                if (value >= min) {
                    return value;
                } else {
                    display("⚠️ Minimum value should be %d.\n", min);

                }
            } catch (InputMismatchException e) {
                display("⚠️ Invalid input. Please enter a valid number.");
                input.nextLine(); // Clear invalid input
            }
        }
    }
    public void display(String message){
        System.out.println(message);
    }
    public void display(String message, Object... args){
        System.out.printf(message,args);
    }

}
