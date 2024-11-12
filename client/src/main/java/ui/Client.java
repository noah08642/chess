package ui;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import chess.ChessGame;
import model.GameData;
import network.ServerFacade;
import request.*;
import result.LogRegResult;


public class Client {
    ServerFacade server;
    private final String serverUrl;
    private String authToken;
    private List<GameData> gameList;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        authToken = null;
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
            if (authToken!=null) {
                switch (input) {
                    case 1 -> login();
                    case 2 -> register();
//                    case 3 -> help();
                };
            }
            else {
                switch (input) {
                    case 1 -> logout();
                    case 2 -> createGame();
                    case 3 -> listGames();
                    case 4 -> joinGame();
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
                    GameData game = gameList.get(i);
                    System.out.println(" " + i + 1 + " " + game.gameName());

                    String blackName = (game.blackUsername()==null) ? "Available" : game.blackUsername();
                    String whiteName = (game.whiteUsername()==null) ? "Available" : game.whiteUsername();

                    System.out.print("   - " + blackName);
                    System.out.print("   - " + whiteName);
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

    private GameData gameSelector() {
        listGames();
        if (gameList.isEmpty()) {
            System.out.println("No available games.  Create a game and come back.");
            return null;
        }

        System.out.println("Enter a number to select a game");
        int input = getInt() - 1;
        if (input >= gameList.size()) {
            System.out.println("Game does not exist, select another game");
            return gameSelector();
        }
        GameData game = gameList.get(input);
        if (game.blackUsername()!= null && game.whiteUsername()!=null) {
            System.out.print("Not available to join.  Select another game. \n");
            return gameSelector();
        }
        return game;
    }

    private ChessGame.TeamColor colorSelector(GameData game) {
        System.out.println("Enter a number to select a color");
        System.out.print("\n1 WHITE \n2 BLACK\n");
        int input = getInt();
        if (input != 1 && input != 0) {
            System.out.print("\n Invalid\n");
            colorSelector(game);
        }
        String choice = (input == 0) ? game.blackUsername() : game.whiteUsername();
        if (choice == null) {
            System.out.print("\n Invalid\n");
            colorSelector(game);
        }
        ChessGame.TeamColor teamColor = (input == 0) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        return teamColor;
    }

    public void joinGame() {

        GameData game = gameSelector();

        if (game==null) {
            System.out.println("No available games.  Create one and then come back.");
            return;
        }

        int id = game.gameID();

        ChessGame.TeamColor teamColor = colorSelector(game);

        try {
            server.joinGame(new JoinGameRequest(teamColor, id, authToken));
        } catch (Exception e){
            System.out.println("unable to join game: " + e.getMessage());
        }
    }

    public void logout() {
        try {
            server.logout(new LogoutRequest(authToken));
        } catch (Exception e){
            System.out.println("unable to logout: " + e.getMessage());
        }
    }

    public void observe() {
        listGames();
        GameData game = gameSelector();
        System.out.print(game.toString());
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
        if (authToken != null) {
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
