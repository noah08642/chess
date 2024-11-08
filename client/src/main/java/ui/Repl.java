package ui;

import java.util.Scanner;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to the chess menu. Sign in to start.");

        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean isNotQuit = true;
        while(isNotQuit) {
            System.out.print("\n" + ">>> ");
            System.out.print(client.menu());
            choice = scanner.nextInt();
            isNotQuit = client.eval(choice);
        }



        System.out.println();
    }
}