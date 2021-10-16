package it.unive.quadcore.smartmeal.local;

/**
 * Eccezione lanciata quando il Customer chiede un Table malgrado gliene fosse già stato assegnato uno
 */
public class AlreadyAssignedTableException extends TableException {
    AlreadyAssignedTableException(String message) {
        super(message);
    }
}
