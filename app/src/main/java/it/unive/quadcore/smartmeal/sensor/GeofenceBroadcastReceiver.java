package it.unive.quadcore.smartmeal.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Objects;

/**
 * Classe per ricezione eventi geofence
 */
class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private final static String TAG = "GeofenceReceiver";

    // Ricevuto evento geofence
    public void onReceive(Context context, Intent intent) {
        // Geofence event
        Log.d(TAG, "onReceive");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) { // Errore evento geofence
            Log.e(TAG,"Si è verificato un errore nell'evento geofence");
            return;
        }

        // Geofence transition type
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Testo geofence transition type:  vedo se è quella che mi interessa. A me interessa solo entrata.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Esecuzione della callback onEntranceCallback
            Objects.requireNonNull(SensorDetector.onEntranceCallback);
            SensorDetector.onEntranceCallback.run();
        }
    }
}
