package it.unive.quadcore.smartmeal.communication.confirmation;

import java.io.Serializable;

/**
 * Confirmation è concettualmente simile a Response, nello stesso modo in cui un Supplier è
 * concettualmente simile a una Function.
 * Un oggetto di tipo Confirmation è usato nella comunicazione per confermare qualcosa.
 * Classe immutable.
 *
 * La classe implementa Serializable, così da poter trasformare le sue istanze in byte e viceversa.
 *
 * @param <E> possibile tipo di eccezione, nel caso la conferma sia negata
 */
public class Confirmation<E extends Exception> implements Serializable {

    /**
     * Costruttore di Confirmation
     */
    public Confirmation() { }


    /**
     * Ritorna normalmente se è una Confirmation, solleva una eccezione se è una ConfirmationDenied
     *
     * @throws E se è una ConfirmationDenied
     */
    public void obtain() throws E {}
}
