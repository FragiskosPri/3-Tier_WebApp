package mainpackage;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.*;

public class Utilities {
    public static int getChoice (int min, int max) {

        int choice = 0;
        do {
          choice = inputInt();
            if (choice < min || choice > max) {
                System.out.println("Invalid input! Please enter a number between " + min + " and " + max);
            }
        } while (choice < min || choice > max);
        return choice;
    }

    public static boolean isNDigits(long number, int n) {
        String numberStr = String.valueOf(number);
        return numberStr.length() == n;
    }

    public static long inputLong() {
        Scanner scanner = new Scanner(System.in);
        long x;

        while (true) {
            try {
                x = scanner.nextLong();
                break;
            } catch (Exception e) {
                System.out.println("Please insert a number!");
                scanner.nextLine();
            }
        }
        return x;
    }

    public static int inputInt() {
        Scanner scanner = new Scanner(System.in);
        int x;

        while (true) {
            try {
                x = scanner.nextInt();
                break;
            } catch (Exception e) {
                System.out.println("Please insert a number!");
                scanner.nextLine();
            }
        }
        return x;
    }

    public static float  inputFloat() {
        Scanner scanner = new Scanner(System.in);
        float x;

        while (true) {
            try {
                x = scanner.nextFloat();
                break;
            } catch (Exception e) {
                System.out.println("Input is wrong!");
                scanner.nextLine();
            }
        }
        return x;
    }

    public static int findClientIndex(ArrayList<Client> clients, int AFM){
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getAFM() == AFM){
                return i;
            }
        }
        System.out.println("Client not found!");
        return -1;
    }
}
