package it.unive.quadcore.smartmeal.communication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Classe che implementa le callback relative alla ricezione dei messaggi.
 */
public abstract class MessageListener extends PayloadCallback {
    private static final String TAG = "MessageListener";

    @Override
    public void onPayloadReceived(@NonNull String endpointId, Payload payload) {
        if (payload.getType() != Payload.Type.BYTES) {
            Log.wtf(TAG, "Received a non byte Payload");
            return;
        }

        // This always gets the full data of the payload. Will be null if it's not a BYTES
        // payload. You can check the payload type with payload.getType().
        try {
            final byte[] receivedBytes = payload.asBytes();
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(receivedBytes));

            Message message = (Message) objectInputStream.readObject();
            Log.i(TAG, "Message received: " + message.getRequestType());

            onMessageReceived(endpointId, message);

        } catch (IOException e) {
            Log.wtf(TAG, "Unexpected input IOException: " + e);
            throw new AssertionError("Unexpected input IOException");
        } catch (ClassNotFoundException | ClassCastException e) {
            Log.wtf(TAG, "Payload was not a Message: " + e);
            throw new AssertionError("Payload was not a Message");
        }
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
        // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
        // after the call to onPayloadReceived().
    }

    /**
     * Callback chiamata quando viene ricevuto un messaggio Nearby
     *
     * @param endpointId id nearby del dispositivo che ha inviato il messaggio
     * @param message messaggio ricevuto
     */
    protected abstract void onMessageReceived(String endpointId, Message message);
}
