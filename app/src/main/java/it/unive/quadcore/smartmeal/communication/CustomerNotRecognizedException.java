package it.unive.quadcore.smartmeal.communication;

/**
 * Eccezione che segnala che un Customer non è stato riconosciuto
 * dal manager, ovvero non è ancora stato memorizzato nell'elenco
 * dei Customer, probabilmente perchè non è stato ricevuto il messaggio
 * di contenente il nome del suddetto Customer.
 */
public class CustomerNotRecognizedException extends Exception {

    /**
     * Costruttore con messaggio di default
     */
    public CustomerNotRecognizedException() {
        this("The customer was not recognized");
    }

    /**
     * Costruttore con messaggio a scelta
     *
     * @param message il messaggio dell'eccezione
     */
    public CustomerNotRecognizedException(String message) {
        super(message);
        throw new IllegalStateException();
    }
}
