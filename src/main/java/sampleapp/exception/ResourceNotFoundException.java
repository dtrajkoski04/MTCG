package sampleapp.exception;

/**
 * Wird geworfen, wenn eine Ressource (z. B. Pakete oder Karten) nicht existiert.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
