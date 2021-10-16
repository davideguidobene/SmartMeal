package it.unive.quadcore.smartmeal.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.storage.Storage;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.CustomerBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.customer.InsertPersonalDataActivity;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.SendWelcomeNotificationCallback;
import it.unive.quadcore.smartmeal.ui.manager.InsertPasswordActivity;
import it.unive.quadcore.smartmeal.ui.manager.home.ManagerHomeActivity;

import it.unive.quadcore.smartmeal.util.PermissionHandler;

public class SelectAppModeActivity extends AppCompatActivity {

    private static final String TAG = "SelectAppModeActivity";

    private Button customerButton;
    private Button managerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inizializza Storage se non lo è già
        if (!Storage.isInitialized()) {
            Log.i(TAG, "Storage initialized");
            Storage.initializeStorage(this);
        }

        switch (Storage.getApplicationMode()) {
            case UNDEFINED:
                setContentView(R.layout.activity_select_app_mode);
                break;
            case CUSTOMER:
                startActivity(new Intent(this, CustomerBottomNavigationActivity.class));
                finish();
                return;
            case MANAGER:
                  startActivity(new Intent(this, ManagerHomeActivity.class));
                  finish();
                return;
            default:
                throw new IllegalStateException("Unexpected: Storage.getApplicationMode() returned null");
        }

        customerButton = findViewById(R.id.customer_button);
        managerButton = findViewById(R.id.manager_button);

        customerButton.setOnClickListener(v -> {
            // avvia l'activity che richiede i dati dell'utente
            startActivity(new Intent(SelectAppModeActivity.this, InsertPersonalDataActivity.class));
        });

        managerButton.setOnClickListener(v -> {
            // avvia l'activity che richiede la password per accedere alla modalità MANAGER

            startActivity(new Intent(SelectAppModeActivity.this, InsertPasswordActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionHandler.requestAllPermissions(this);

        createWelcomeNotificationChannel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PermissionHandler.getRequestCodeRequiredPermissions()) {
            return;
        }

        if(Storage.getApplicationMode()!=ApplicationMode.CUSTOMER){
            Log.w(TAG, "Not a Customer");
            return;
        }

        if (PermissionHandler.hasNotificationsPermissions(this)) {
            CustomerStorage.setNotificationMode(true);
        }

        if (PermissionHandler.hasSensorsPermissions(this)) {
            CustomerStorage.setSensorMode(true);
        }
    }

    private void createWelcomeNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.welcome_notification_channel_name);
            String description = getString(R.string.welcome_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(SendWelcomeNotificationCallback.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
