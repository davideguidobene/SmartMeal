package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * La classe astratta Communication ha lo scopo di mantenere il codice usato sia nella sottoclasse
 * CustomerCommunication che nella sottoclasse ManagerCommunication in modo da ottimizzare il
 * riutilizzo di codice.
 * Le sottoclassi di questa classe saranno usate allo scopo di presentare un'interfaccia di
 * comunicazione user-friendly basata su Nearby.
 */
abstract class Communication {

    /**
     * Tag per logging
     */
    @NonNull
    private static final String TAG = "Communication";

    /**
     * Strategia usata per la comunicazione Nearby (uno a molti)
     */
    @NonNull
    protected static final Strategy STRATEGY = Strategy.P2P_STAR;

    /**
     * Id utilizzato da Nearby per identificare il servizio (l'applicazione)
     */
    @NonNull
    protected static final String SERVICE_ID = "it.unive.quadcore.smartmeal";

    /**
     * Activity utilizzata da nearby per compiere le sue funzioni
     */
    @Nullable
    protected Activity activity;


    /**
     * Costruttore per Communication (istanziabile solo da una sottoclasse)
     */
    protected Communication() {
        activity = null;
    }

    /**
     * Invia un messaggio al dispositivo identificato da `toEndpointId`
     *
     * @param toEndpointId id che identifica il destinatario
     * @param message messaggio da inviare
     */
    protected void sendMessage(@NonNull String toEndpointId, @NonNull Message message) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // trasforma il messaggio in un array di byte e quindi in un Payload
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(message);
            Payload filePayload = Payload.fromBytes(outputStream.toByteArray());

            // invia il messaggio usando Nearby
            assert activity != null;
            Nearby.getConnectionsClient(activity).sendPayload(toEndpointId, filePayload);
        } catch (IOException e) {
            // non dovrebbe mai accadere usando ByteArrayOutputStream
            Log.wtf(TAG, "Unexpected output IOException: " + e);
            throw new AssertionError("Unexpected output IOException");
        }
    }

}
