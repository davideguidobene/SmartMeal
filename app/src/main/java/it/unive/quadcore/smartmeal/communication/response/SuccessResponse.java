package it.unive.quadcore.smartmeal.communication.response;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Rappresenta una risposta con successo.
 * Vedi {@link Response} per maggiori dettagli.
 *
 * @param <T> è il tipo di ritorno del metodo che si vuole emulare
 * @param <E> è il tipo di eccezione che può essere lanciata dal metodo che si vuole emulare
 */
public class SuccessResponse<T extends Serializable, E extends Exception> implements Response<T, E> {

    /**
     * Il contenuto della risposta
     */
    @NonNull
    private final T content;


    /**
     * Costruttore di SuccessResponse
     *
     * @param content il contenuto della risposta
     */
    public SuccessResponse(@NonNull T content) {
        this.content = content;
    }


    /**
     * Restituisce il contenuto della risposta
     *
     * @return il contenuto della risposta
     */
    @NonNull
    @Override
    public T getContent() {
        return content;
    }
}
