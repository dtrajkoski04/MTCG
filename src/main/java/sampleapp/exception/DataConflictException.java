package sampleapp.exception;

/**
 * Wird geworfen, wenn ein Benutzername bereits existiert.
 */
public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
