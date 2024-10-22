package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class InvalidAuthException extends DataAccessException {
    public InvalidAuthException() {
        super("Error: unauthorized");
    }
}
