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
    private String username;

    public Client(String serverUrl) throws Exception {
        try {server = new ServerFacade(serverUrl);
        } catch( Exception ex) { System.out.println(ex.getMessage());}
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
            if (authToken==null) {
                switch (input) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> help();
                };
            }
            else {
                switch (input) {
                    case 1 -> logout();
                    case 2 -> createGame();
                    case 3 -> listGames();
                    case 4 -> joinGame();
                    case 5 -> observe();
                    case 6 -> help2();
                };
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    private void help() {
        System.out.println("Enter 0 to quit, enter 1 to login, enter 2 to register, or enter 3 to see this again.");
    }

    private void help2() {
        System.out.println("Enter 1 to logout, 2 to create game, 3 to list games, 4 to join a game, 5 ot observe, and 6 to see this menu again.");
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
            username = user;
        }
        catch (Exception ex) {
            //System.out.println(ex.getMessage());
            System.out.println("Unauthorized");
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
            username = user;
        }
        catch (Exception ex) {
            //System.out.println(ex.getMessage());
            System.out.println("Invalid");
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
                    System.out.println(" " + (i + 1) + " " + game.gameName());

                    String whiteName = (game.whiteUsername()==null) ? "Available" : game.whiteUsername();
                    String blackName = (game.blackUsername()==null) ? "Available" : game.blackUsername();

                    System.out.println("   - White: " + whiteName);
                    System.out.println("   - Black: " + blackName);

                }
            }
        } catch(Exception e) {
            System.out.println("error in listing games (could be bad authToken)");
        }
    }

    public void createGame() {
        System.out.println("Enter game name: ");
        String gameName = getInput();
        CreateGameRequest request = new CreateGameRequest(gameName, authToken);
        try {
            server.createGame(request);
        } catch(Exception e) {
            //System.out.println(ex.getMessage());
            System.out.println("Unable to create game.");        }
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
        if (input != 1 && input != 2) {
            System.out.print("\n Invalid\n");
            colorSelector(game);
        }
        String choice = (input == 2) ? game.blackUsername() : game.whiteUsername();
        if (choice != null) {
            System.out.print("\n Taken\n");
            colorSelector(game);
        }
        ChessGame.TeamColor teamColor = (input == 2) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
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
            GameClient gameClient = new GameClient(server, authToken, username, game);
            gameClient.run();

            BoardPrinter printer = new BoardPrinter();
            printer.print(ChessGame.TeamColor.WHITE, game.getGame().getBoard().getBoard());
            printer.print(ChessGame.TeamColor.BLACK, game.getGame().getBoard().getBoard());
        } catch (Exception e){
            //System.out.println(ex.getMessage());
            System.out.println("Unable to join game");        }
    }

    public void logout() {
        try {
            server.logout(new LogoutRequest(authToken));
            authToken = null;
        } catch (Exception e){
            //System.out.println(ex.getMessage());
            System.out.println("Unable to logout");        }
    }

    public void observe() {
        listGames();
        if (gameList.isEmpty()) {
            System.out.println("No available games.  Create a game and come back.");
            return;
        }

        System.out.println("Enter a number to select a game");
        int input = getInt() - 1;
        if (input >= gameList.size()) {
            System.out.println("Game does not exist, select another game");
            observe();
        }
        GameData game = gameList.get(input);
        BoardPrinter printer = new BoardPrinter();
        printer.print(ChessGame.TeamColor.WHITE, game.getGame().getBoard().getBoard());
        printer.print(ChessGame.TeamColor.BLACK, game.getGame().getBoard().getBoard());
        System.out.print("Joined game :)");
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
        if (authToken == null) {
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
                6 Help
                """;
        }
    }


}
