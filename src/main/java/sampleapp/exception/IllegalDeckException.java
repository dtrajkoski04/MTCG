package sampleapp.exception;

/**
 * Wird geworfen, wenn ein ungültiges Deck konfiguriert wurde.
 */
public class IllegalDeckException extends RuntimeException {
    public IllegalDeckException(String message) {
        super(message);
    }
}
