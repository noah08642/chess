package websocket.commands;

/**
 * Represents a CONNECT command sent by a user to join a game as a player or observer.
 */
public class ResignCommand extends UserGameCommand {

    public ResignCommand(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }

    @Override
    public String toString() {
        return "ConnectCommand{" +
                "commandType=" + getCommandType() +
                ", authToken='" + getAuthToken() + '\'' +
                ", gameID=" + getGameID() +
                '}';
    }
}
