package sampleapp.exception;

/**
 * Wird geworfen, wenn ein Benutzer nicht gefunden wird.
 */
public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
    super(message);
  }
}
