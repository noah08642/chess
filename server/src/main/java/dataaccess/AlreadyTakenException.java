package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException() {
        super("Error: already taken");
    }

    // Constructor with message and cause (to include a backtrace)
    public AlreadyTakenException(Throwable cause) {
        super("Error: already taken", cause);
    }
}
