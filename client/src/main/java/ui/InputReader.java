package ui;

import java.util.Scanner;

public class InputReader {
    static public String getInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    static public int getInteger(String prompt) {
        return getInteger(prompt, true);
    }

    static public int getInteger(String prompt, boolean printPrompt) {
        int result = 0;
        boolean valid = false;

        while (!valid) {
            if (printPrompt) {System.out.print(prompt);}
            String input = getInput();

            try {
                // Try to parse the input as an integer
                result = Integer.parseInt(input);
                valid = true; // Parsing succeeded
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        return result;
    }
}
