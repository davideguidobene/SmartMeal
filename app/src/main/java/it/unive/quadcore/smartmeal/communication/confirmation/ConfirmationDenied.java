package it.unive.quadcore.smartmeal.communication.confirmation;

/**
 * Sottoclasse di Confirmation, un oggetto di tipo ConfirmationDenied è creato quando si vuole
 * comunicare che una richiesta è fallita e quindi la Conferma richiesta dev'essere negata.
 * Classe immutable.
 *
 * @param <E> tipo del parametro dell'eccezione che si vuole mandare quando qualcuno prova a
 *           ottenere la richiesta (passata direttamente al momento della costruzione)
 */
public class ConfirmationDenied<E extends Exception> extends Confirmation<E> {

    /**
     * Eccezione che verrà sollevata quando verrò chiamato `obtain`
     */
    private final E exception;


    /**
     * Costruttore di ConfirmationDenied
     *
     * @param exception l'eccezione che verrà sollevata quando verrò chiamato `obtain`
     */
    public ConfirmationDenied(E exception) {
        this.exception = exception;
    }


    /**
     * Solleva sempre un eccezione
     *
     * @throws E sempre
     */
    @Override
    public void obtain() throws E {
        throw exception;
    }
}
