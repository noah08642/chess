package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class InvalidAuthException extends DataAccessException {
    public InvalidAuthException() {
        super("Error: unauthorized");
    }

    // Constructor with message and cause (to include a backtrace)
    public InvalidAuthException(Throwable cause) {
        super("Error: unauthorized", cause);
    }
}
