package SDMConsole;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Validation {
    public static int getValidChoice(int minChoice, int maxChoice) {
        boolean succeed = false;
        Scanner s = new Scanner(System.in);
        int choice = 0;
        do {
            try {
                choice = s.nextInt();
                if (!isInRange(choice,minChoice,maxChoice)) {
                    System.out.println("You must choose a number between " + minChoice + " to " + maxChoice);
                } else {
                    succeed = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("You must enter an integer!");
                s.nextLine(); //empty buffer
            } catch (Exception e) {
                System.out.println("An unknown occurred, please try again.");
                s.nextLine(); //empty buffer
            }
        }
        while (!succeed);

        return choice;
    }

    public static boolean checkIfLocationInRange(int x, int y, int minCoordinate, int maxCoordinate) {
        boolean inRange = true;
        if (!isInRange(x, minCoordinate, maxCoordinate)) {
            System.out.println("X coordinate is not in range!");
            inRange = false;
        }
        if (!isInRange(y, minCoordinate, maxCoordinate)) {
            System.out.println("Y coordinate is not in range!");
            inRange = false;
        }

        return inRange;
    }

    private static boolean isInRange(int target, int min, int max){
        return target >= min && target <= max;
    }
}
