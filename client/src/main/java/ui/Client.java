package ui;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import chess.ChessGame;
import model.GameData;
import network.ServerFacade;
import request.CreateGameRequest;
import request.ListRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.LogRegResult;


public class Client {
    ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private List<GameData> gameList;

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
                };
            }
            else {
                switch (input) {
//                    case 1 -> logout();
                    case 2 -> createGame();
                    case 3 -> listGames();
//                    case 4 -> joinGame();
//                    case 5 -> observeGame();
                };
            }

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
            LogRegResult result = server.login(request);
            authToken = result.authToken();
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
            LogRegResult result = server.register(request);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            menu();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void listGames() {
        try {
            List<GameData> gameList = server.listGames(new ListRequest(authToken));
            this.gameList = gameList;
            if(gameList == null) {
                System.out.println("No games available");
            }
            else {
                System.out.println("\nGames: ");
                for(int i = 0; i < gameList.size(); ++i) {
                    System.out.println(" " + i + " " + gameList.get(i).gameName());
                }
            }
        } catch(IOException e) {
            System.out.println("error in listing games (could be bad authToken");
        }
    }

    public void createGame() {
        System.out.println("Enter game name: ");
        String gameName = getInput();
        CreateGameRequest request = new CreateGameRequest(gameName, authToken);
        try {
            server.createGame(request);
        } catch(Exception e) {
            System.out.println("unable to create game: " + e.getMessage());
        }
    }

    public void joinGame() {
        listGames();
        if(gameList.isEmpty()) {
            System.out.println("No available games.  Create a game and come back.");
            return;
        }

        System.out.println("Enter a number to select a game");
        int input = getInt();
        while (input > gameList.size()) {
            System.out.println("Game does not exist, select another game");
            input = getInt();
        }
        int id = gameList.get(input).gameID();


        System.out.println("Enter a number to select a color");
        System.out.print("\n1 WHITE \n2 BLACK\n");
        input = getInt();
        while (input != 1 && input != 0) {
            System.out.print("\n Gosh dangit select a given number\n1 WHITE \n2 BLACK\n");
            input = getInt();
        }
        ChessGame.TeamColor teamColor;
        if(input ==1) {teamColor = ChessGame.TeamColor.WHITE;}
        else { teamColor = ChessGame.TeamColor.BLACK;}


    }


    private String getInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private int getInt() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
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
