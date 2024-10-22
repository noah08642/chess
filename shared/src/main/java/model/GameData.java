package model;

import chess.ChessGame;

import java.util.Objects;

public final class GameData {
    private final int gameID;
    private final ChessGame game;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public void changeGameName(String newName) {
        gameName = newName;
    }

    public int gameID() {
        return gameID;
    }

    public String whiteUsername() {
        return whiteUsername;
    }

    public String blackUsername() {
        return blackUsername;
    }

    public void addUser(ChessGame.TeamColor color, String user) {
        if (color == ChessGame.TeamColor.BLACK) {
            blackUsername = user;
        } else {
            whiteUsername = user;
        }

    }

    public String gameName() {
        return gameName;
    }

    public ChessGame game() {
        return game;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GameData) obj;
        return this.gameID == that.gameID &&
                Objects.equals(this.whiteUsername, that.whiteUsername) &&
                Objects.equals(this.blackUsername, that.blackUsername) &&
                Objects.equals(this.gameName, that.gameName) &&
                Objects.equals(this.game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public String toString() {
        return "GameData[" +
                "gameID=" + gameID + ", " +
                "whiteUsername=" + whiteUsername + ", " +
                "blackUsername=" + blackUsername + ", " +
                "gameName=" + gameName + ", " +
                "game=" + game + ']';
    }

}