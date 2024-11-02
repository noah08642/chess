package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class BadRequestException extends DataAccessException {
    public BadRequestException() {
        super("Error: bad request");
    }

    // Constructor with message and cause (to include a backtrace)
    public BadRequestException(Throwable cause) {
        super("Error: bad request", cause);
    }
}
