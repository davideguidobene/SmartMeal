package it.unive.quadcore.smartmeal.local;

/**
 * Classe che rappresenta le eccezioni legate ai Table. E' un tipo di eccezione a controllo obbligatorio.
 */
public class TableException extends Exception { // checked

    TableException(String message){
        super(message);
    }

}
