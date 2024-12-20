import ui.Client;
import ui.Repl;

import java.util.Scanner;

import static ui.InputReader.getInteger;

public class ClientMain {

    public static void main(String[] args) throws Exception {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        run(serverUrl);
    }

    public static void run(String serverUrl) throws Exception {
        Client client = new Client(serverUrl);

        System.out.println("\uD83D\uDC36 Welcome to the chess menu. Sign in to start.");

        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean isNotQuit = true;
        while(isNotQuit) {
            choice = getInteger("\n" + ">>> " + client.menu());
            isNotQuit = client.eval(choice);
        }

        System.out.println();
    }
}