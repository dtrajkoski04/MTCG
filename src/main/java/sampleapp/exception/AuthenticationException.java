package sampleapp.exception;

/**
 * Wird geworfen, wenn eine Authentifizierung fehlschlägt (z. B. falsche Login-Daten).
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
