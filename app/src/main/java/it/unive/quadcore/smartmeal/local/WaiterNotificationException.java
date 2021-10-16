package it.unive.quadcore.smartmeal.local;

/**
 * Classe che rappresenta le eccezioni legate alle waiter notifications. E' un tipo di eccezione a controllo obbligatorio.
 */

public class WaiterNotificationException extends Exception{ // checked

    WaiterNotificationException(String message){
        super(message);
    }

}
