package sampleapp.exception;

/**
 * Wird geworfen, wenn der Benutzer nicht genug virtuelle Währung hat.
 */
public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message) {
    super(message);
  }
}
