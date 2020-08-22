package SDMConsole;

import SDMSystem.system.SDMSystem;
import SDMSystemDTO.product.DTOProductInStore;
import SDMSystemDTO.store.DTOStore;

import java.util.InputMismatchException;
import java.util.Map;
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

    public static int getValidPositiveInteger(){
        Scanner s = new Scanner(System.in);
        int res = 0;
        boolean succeeded = false;
        while(!succeeded){
            try{
                res = s.nextInt();
                if(res > 0){
                    succeeded = true;
                }
                else{
                    System.out.println("You must enter a positive number!");
                }
            }
            catch (InputMismatchException ex){
                System.out.println("You must enter an integer!");
                s.nextLine();
            }
        }

        return res;
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

    public static float getValidPositiveNumber() {
        Scanner s = new Scanner(System.in);
        float res = 0;
        boolean succeeded = false;
        while(!succeeded){
            try{
                res = s.nextFloat();
                if(res > 0){
                    succeeded = true;
                }
                else{
                    System.out.println("You must enter a positive number!");
                }
            }
            catch (InputMismatchException ex){
                System.out.println("You must enter a float number!");
                s.nextLine();
            }
        }

        return res;
    }

    public static boolean getValidYesOrNoAnswer() {
        Scanner s = new Scanner(System.in);
        char answer = 0;
        boolean validAnswer = false;
        while(!validAnswer) {
            answer = s.next().charAt(0);
            if (answer == 'Y' || answer == 'y' || answer == 'N' || answer == 'n'){
                validAnswer = true;
            }
            else{
                System.out.println("You must enter Y or N!");
            }
        }

        return answer == 'Y' || answer == 'y';
    }

    public static boolean isQ(String input) {
        return ((input.charAt(0) == 'Q' || input.charAt(0) == 'q') && input.length() == 1);
    }

    public static DTOStore chooseValidStore(SDMSystem sdmSystem) {
        boolean succeeded = false;
        DTOStore chosenStore = null;
        Scanner s = new Scanner(System.in);
        do {
            try {
                int chosenStoreSerialNumber = s.nextInt();
                chosenStore = sdmSystem.getStoreFromStores(chosenStoreSerialNumber);
                if (chosenStore != null) {
                    succeeded = true;
                } else {
                    System.out.println("No such store in the system! Please try again!");
                }
            } catch (InputMismatchException e) {
                System.out.println("You must enter an integer!");
                s.nextLine();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                s.nextLine();
            }
        }
        while (!succeeded);

        return chosenStore;
    }

    public static DTOProductInStore chooseValidProductFromStore(DTOStore storeToUpdate) {
        boolean succeeded = false;
        DTOProductInStore chosenProduct = null;
        Scanner s = new Scanner(System.in);
        do {
            try {
                int chosenProductSerialNumber = s.nextInt();
                chosenProduct = storeToUpdate.getProductFromStore(chosenProductSerialNumber);
                if (chosenProduct != null) {
                    succeeded = true;
                } else {
                    System.out.println("No such product in the store! Please try again!");
                }
            } catch (InputMismatchException e) {
                System.out.println("You must enter an integer!");
                s.nextLine();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                s.nextLine();
            }
        }
        while (!succeeded);

        return chosenProduct;
    }
}
