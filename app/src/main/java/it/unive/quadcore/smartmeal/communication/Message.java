package it.unive.quadcore.smartmeal.communication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe che rappresenta un messaggio, ovvero l'entità che viene scambiata
 * in ogni Payload di Nearby.
 * La classe implementa Serializable, così da poter trasformare le sue
 * istanze in byte e viceversa.
 * Le istanze sono immutabili.
 */
class Message implements Serializable {

    /**
     * Rappresenta la tipologia del messaggo
     */
    @NonNull
    private final RequestType requestType;

    /**
     * Il contenuto del messaggio
     */
    @Nullable
    private final Serializable content;

    /**
     * Costruttore di un messaggio
     *
     * @param requestType la tipologia del messaggio
     * @param content il contenuto del messaggio (può essere null)
     */
    Message(@NonNull RequestType requestType, @Nullable Serializable content) {
        Objects.requireNonNull(requestType);

        this.requestType = requestType;
        this.content = content;
    }

    /**
     * Getter per la tipologia del messaggio
     *
     * @return un `RequestType` che rappresenta la tipologia del messaggio
     */
    @NonNull
    RequestType getRequestType() {
        return requestType;
    }

    /**
     * Getter per il contenuto del messaggio
     *
     * @return un Serializable rappresentante il contenuto del messaggio
     */
    @Nullable
    Serializable getContent() {
        return content;
    }
}
