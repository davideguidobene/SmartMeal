package it.unive.quadcore.smartmeal.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe che gestisce i permessi richiesti per utilizzare le vaie funzioni dell'applicazione.
 * Classe non instanziabile.
 */
public class PermissionHandler {

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    /**
     * Costruttore privato: non instanziabile
     */
    private PermissionHandler() {
        throw new AssertionError("Non-instantiable class");
    }


    /**
     * Restituisce i permessi necessari per Nearby.
     *
     * @return i permessi necessari per Nearby
     */
    private static String[] getNearbyRequiredPermissions() {
        return new String[] {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
    }


    /**
     * Restituisce i permessi necessari per le notifiche.
     *
     * @return i permessi necessari per le notifiche
     */
    private static String[] getNotificationsRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        } else {
            return new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }
    }


    /**
     * Restituisce i permessi necessari per i sensori.
     *
     * @return i permessi necessari per i sensori
     */
    private static String[] getSensorsRequiredPermissions() {
        return new String[] {};
    }


    private static String[] getAllRequiredPermissions() {
        ArrayList<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.addAll(Arrays.asList(getNearbyRequiredPermissions()));
        requiredPermissions.addAll(Arrays.asList(getNotificationsRequiredPermissions()));
        requiredPermissions.addAll(Arrays.asList(getSensorsRequiredPermissions()));

        return requiredPermissions.toArray(new String[0]);
    }


    /**
     * Restituisce il codice della richiesta di permessi.
     *
     * @return il codice della richiesta di permessi
     */
    public static int getRequestCodeRequiredPermissions() {
        return REQUEST_CODE_REQUIRED_PERMISSIONS;
    }


    /**
     * Verifica se l'applicazione ha i permessi per usare Nearby.
     *
     * @param context il contesto da cui sono richiesti
     */
    public static boolean hasNearbyPermissions(Context context) {
        return hasPermission(
                context,
                getNearbyRequiredPermissions()
        );
    }


    /**
     * Richiede tutti i permessi per usare le Nearby.
     *
     * @param activity l'activity da cui sono richiesti
     */
    public static void requestNearbyPermissions(Activity activity) {
        requestPermissions(
                activity,
                getNearbyRequiredPermissions()
        );
    }


    /**
     * Verifica se l'applicazione ha i permessi per usare le notifiche.
     *
     * @param context il contesto da cui sono richiesti
     */
    public static boolean hasNotificationsPermissions(Context context) {
        return hasPermission(
                context,
                getNotificationsRequiredPermissions()
        );
    }


    /**
     * Richiede tutti i permessi per usare le notifiche.
     *
     * @param activity l'activity da cui sono richiesti
     */
    public static void requestNotificationsPermissions(Activity activity) {
        requestPermissions(
                activity,
                getNotificationsRequiredPermissions()
        );
    }


    /**
     * Richiede tutti i permessi per usare i sensori.
     *
     * @param context l'activity da cui sono richiesti
     */
    public static boolean hasSensorsPermissions(Context context) {
        return hasPermission(
                context,
                getSensorsRequiredPermissions()
        );
    }


    /**
     * Richiede tutti i permessi per usare i sensori.
     *
     * @param activity l'activity da cui sono richiesti
     */
    public static void requestSensorsPermissions(Activity activity) {
        requestPermissions(
                activity,
                getSensorsRequiredPermissions()
        );
    }


    /**
     * Richiede tutti i permessi.
     *
     * @param activity l'activity da cui sono richiesti
     */
    public static void requestAllPermissions(Activity activity) {
        requestPermissions(
                activity,
                getAllRequiredPermissions()
        );
    }


    /**
     * Verifica se l'applicazione ha i permessi.
     *
     * @param context il ccontesto da cui sono richiesti
     * @param permissions i permessi da verificare
     *
     * @return true se l'applicazione ha tutti i permessi, false altrimenti
     */
    private static boolean hasPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * Richiede i permessi specificati.
     *
     * @param activity l'activity da cui sono richiesti
     * @param permissions i permessi da richiedere
     */
    private static void requestPermissions(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(activity, permissions)) {
                activity.requestPermissions(permissions, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
    }

}
