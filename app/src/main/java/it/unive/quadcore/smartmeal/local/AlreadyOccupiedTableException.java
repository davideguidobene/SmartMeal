package it.unive.quadcore.smartmeal.local;

/**
 * Eccezione lanciata quando il Customer chiede un Table che risulta essere già occupato
 */
public class AlreadyOccupiedTableException extends TableException {
    AlreadyOccupiedTableException(String message) {
        super(message);
    }
}
