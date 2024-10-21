package request;

import chess.ChessGame;

import java.util.Objects;

public final class JoinGameRequest {
    private final ChessGame.TeamColor playerColor;
    private final int gameID;
    private String authToken;

    public JoinGameRequest(ChessGame.TeamColor playerColor, int gameID, String authToken) {
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.authToken = authToken;
    }

    public ChessGame.TeamColor playerColor() {
        return playerColor;
    }

    public int gameID() {
        return gameID;
    }

    public String authToken() {
        return authToken;
    }

    public void addAuth(String auth) {
        authToken = auth;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JoinGameRequest) obj;
        return Objects.equals(this.playerColor, that.playerColor) &&
                this.gameID == that.gameID &&
                Objects.equals(this.authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerColor, gameID, authToken);
    }

    @Override
    public String toString() {
        return "JoinGameRequest[" +
                "playerColor=" + playerColor + ", " +
                "gameID=" + gameID + ", " +
                "authToken=" + authToken + ']';
    }

}
