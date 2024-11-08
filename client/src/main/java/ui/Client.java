package ui;

import java.util.Scanner;

import network.ServerFacade;
import request.LoginRequest;
import request.RegisterRequest;


public class Client {
    ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    // update this..
    // if they don't have authorization, send them to menu 1 outputs
    // if the do, send them to menu 2 outputs
    // cases are numbers
    public boolean eval(int input) {
        try {
            if (input == 0) {
                return false;
            }
            if (state==State.SIGNEDOUT) {
                switch (input) {
                    case 1 -> login();
                    case 2 -> register();
//                    case 3 -> help();
//                    case 0 -> quit();
                };
            }
//            else {
//                return switch (input) {
//                    case 1 -> logout();
//                    case 2 -> createGame();
//                    case 3 -> listGames();
//                    case 4 -> joinGame();
//                    case 5 -> observeGame();
//                };
//            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    public void login() {
        try {
            System.out.println("Enter username: ");
            String user = getInput();
            System.out.println("Enter password: ");
            String pass = getInput();
            LoginRequest request = new LoginRequest(user, pass);
            server.login(request);
            state = State.SIGNEDIN;
            menu();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void register() {
        try {
            System.out.println("Enter username: ");
            String user = getInput();
            System.out.println("Enter password: ");
            String pass = getInput();
            System.out.println("Enter email: ");
            String email = getInput();
            RegisterRequest request = new RegisterRequest(user, pass, email);
            server.register(request);
            state = State.SIGNEDIN;
            menu();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String getInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public String menu() {
        if (state == State.SIGNEDOUT) {
            return """
                    Enter a number to select:
                    
                    1 Login
                    2 Register
                    3 Help
                    0 Quit
                    """;
        }
        else {
            return """
                Enter a number to select:
                
                1 Logout
                2 Create Game
                3 List Games
                4 Join Game
                5 Observe Game
                0 Quit
                """;
        }
    }




    // this does all the output for menu stuff.  and it calls the BoardPrinter...

    // it should have multiple levels of menus...

    // you need to keep track of whether they're signed in or not.
    // the easiest way is to check if authToken is null.  If it is, send them the non-logged-in menu.
}
