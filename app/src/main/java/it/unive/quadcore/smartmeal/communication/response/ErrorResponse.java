package it.unive.quadcore.smartmeal.communication.response;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Rappresenta una risposta con errore.
 * Vedi {@link Response} per maggiori dettagli.
 *
 * @param <T> è il tipo di ritorno del metodo che si vuole emulare
 * @param <E> è il tipo di eccezione che può essere lanciata dal metodo che si vuole emulare
 */
public class ErrorResponse<T extends Serializable, E extends Exception> implements Response<T, E> {

    /**
     * l'eccezione che verrà sollevata quando verrò chiamato `getContent`
     */
    @NonNull
    private final E exception;


    /**
     * Costruttore di ErrorResponse
     *
     * @param exception l'eccezione che verrà sollevata quando verrò chiamato `getContent`
     */
    public ErrorResponse(@NonNull E exception) {
        this.exception = exception;
    }


    /**
     * Solleva sempre l'eccezione E
     *
     * @return non restituisce mai niente in quanto solleva sempre un'eccezione
     *
     * @throws E sempre
     */
    @Override
    public T getContent() throws E {
        throw exception;
    }
}
