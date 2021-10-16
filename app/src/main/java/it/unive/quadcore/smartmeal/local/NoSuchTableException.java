package it.unive.quadcore.smartmeal.local;

/**
 * Eccezione lanciata quando il Customer chiede un Table che non risulta esistere nel Local del Manager
 */
public class NoSuchTableException extends TableException {
    NoSuchTableException(String message) {
        super(message);
    }
}
