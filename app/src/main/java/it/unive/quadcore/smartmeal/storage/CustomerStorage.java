package it.unive.quadcore.smartmeal.storage;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Objects;

public final class CustomerStorage extends Storage {

    private static final String SENSOR_MODE_SHARED_PREFERENCE_KEY = "SensorMode";
    private static final String NOTIFICATION_MODE_SHARED_PREFERENCE_KEY = "NotificationMode";
    /**
     * Rende non instanziabile questa classe.
     */
    private CustomerStorage() {}


    public static void setName(@NonNull String name) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        ApplicationMode applicationMode = getApplicationMode();

        if(applicationMode==ApplicationMode.UNDEFINED)
            throw new StorageException("In the application mode UNDEFINED does not exist a name");
        else if(applicationMode==ApplicationMode.MANAGER) // Non si può cambiare nome lato Manager
            throw new StorageException("The manager can't change the name");

        Objects.requireNonNull(sharedPreferences);

        // Scrivo il nome nello storage. Se non esiste tale preference viene creata.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(NAME_SHARED_PREFERENCE_KEY, name);
        editor.apply();
    }

    public static boolean getSensorMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        if(getApplicationMode()!=ApplicationMode.CUSTOMER)
            throw new StorageException("You must be a customer to do this operation");

        Objects.requireNonNull(defaultSharedPreferences);

        // Preference non esistente (prima creazione della preference). Metto valore di default
        if(!defaultSharedPreferences.contains(SENSOR_MODE_SHARED_PREFERENCE_KEY)) {
            /*SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean(SENSOR_MODE_SHARED_PREFERENCE_KEY,true);
            editor.apply();*/
            setSensorMode(true);
        }

        return defaultSharedPreferences.getBoolean(SENSOR_MODE_SHARED_PREFERENCE_KEY,true);
    }

    public static void setSensorMode(boolean mode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        if(getApplicationMode()!=ApplicationMode.CUSTOMER)
            throw new StorageException("You must be a customer to do this operation");

        Objects.requireNonNull(defaultSharedPreferences);

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Se preference non esiste viene creata
        editor.putBoolean(SENSOR_MODE_SHARED_PREFERENCE_KEY,mode);
        editor.apply();
    }

    public static boolean getNotificationMode() {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        if(getApplicationMode()!=ApplicationMode.CUSTOMER)
            throw new StorageException("You must be a customer to do this operation");

        Objects.requireNonNull(defaultSharedPreferences);

        // Preference non esistente (prima creazione della preference). Metto valore di default
        if(!defaultSharedPreferences.contains(NOTIFICATION_MODE_SHARED_PREFERENCE_KEY)) {
            /*SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean(NOTIFICATION_MODE_SHARED_PREFERENCE_KEY,true);
            editor.apply();*/
            setNotificationMode(true);
        }

        return defaultSharedPreferences.getBoolean(NOTIFICATION_MODE_SHARED_PREFERENCE_KEY,true);
    }

    public static void setNotificationMode(boolean mode) {
        if(!initialized)
            throw new StorageException("The storage hasn't been initialize yet");

        if(getApplicationMode()!=ApplicationMode.CUSTOMER)
            throw new StorageException("You must be a customer to do this operation");

        Objects.requireNonNull(defaultSharedPreferences);

        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        // Se preference non esiste viene creata
        editor.putBoolean(NOTIFICATION_MODE_SHARED_PREFERENCE_KEY,mode);
        editor.apply();
    }

}
