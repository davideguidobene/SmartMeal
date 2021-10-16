package it.unive.quadcore.smartmeal.communication.response;

import java.io.Serializable;

/**
 * La classe Response è usata allo scopo di astrarre la comunicazione intermedia.
 * Per esempio esempio con un oggetto response di tipo Response<Table, TableException>, possiamo
 * replicare il comportamento esatto del metodo getTable(customer) della classe Local:
 * se viene lanciata una TableException dal metodo getTable, la response verrà creata tramite la
 * sottoclasse ErrorResponse di Response,
 * se invece viene restituito un tavolo, la response verrà creata tramite la sottoclasse
 * SuccessResponse di Response
 * In questo modo da lato Customer sarà possibile chiamare il metodo getContent() di response e
 * trattarlo esattamente come se fosse il metodo getTable della classe Local.
 *
 * Un oggetto di tipo Response è quindi creato allo scopo di emulare un metodo visibile
 * solo da lato Manager nel lato Customer o viceversa
 *
 * Classe immutable (e anche le sottoclassi note ErrorResponse e SuccessResponse)
 *
 * La classe implementa Serializable, così da poter trasformare le sue istanze in byte e viceversa.
 *
 * @param <T> è il tipo di ritorno del metodo che si vuole emulare
 * @param <E> è il tipo di eccezione che può essere lanciata dal metodo che si vuole emulare
 */
public interface Response<T extends Serializable, E extends Exception> extends Serializable {

    /**
     * Restituisce il contenuto della risposta se è una {@link SuccessResponse}
     *
     * @return il contenuto della risposta se è una {@link SuccessResponse},
     *
     * @throws E se è una {@link ErrorResponse}
     */
    T getContent() throws E;
}
