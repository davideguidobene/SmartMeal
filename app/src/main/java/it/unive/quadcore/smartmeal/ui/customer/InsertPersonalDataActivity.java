package it.unive.quadcore.smartmeal.ui.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.sensor.SensorDetector;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.CustomerBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.SendWelcomeNotificationCallback;
import it.unive.quadcore.smartmeal.util.PermissionHandler;

public class InsertPersonalDataActivity extends AppCompatActivity {
    private static final String TAG = "InsertPersonalDataAct";

    private EditText nameEditText;
    private Button confirmationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_personal_data);

        nameEditText = findViewById(R.id.name_edit_text);
        confirmationButton = findViewById(R.id.confirmation_button);

        confirmationButton.setOnClickListener(v -> {

            String customerName = nameEditText.getText().toString().trim();

            if (customerName.isEmpty()) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.field_required_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                return;
            }

            CustomerStorage.setApplicationMode(ApplicationMode.CUSTOMER);
            CustomerStorage.setName(customerName);

            Log.i(TAG, "Customer name stored: " + customerName);


            if (CustomerStorage.getNotificationMode()
                    && PermissionHandler.hasNotificationsPermissions(InsertPersonalDataActivity.this)) {
                try {
                    SensorDetector
                            .getInstance()
                            .startEntranceDetection(
                                    new SendWelcomeNotificationCallback(InsertPersonalDataActivity.this),
                                    InsertPersonalDataActivity.this
                            );
                } catch (IllegalStateException e) {
                    Log.w(TAG, "Entrance detection was already activated");
                }
            }

            // avvia l'activity principale del Cliente
            Intent intent = new Intent(InsertPersonalDataActivity.this, CustomerBottomNavigationActivity.class);
            // svuota il backstack
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);
            finish();
        });
    }
}
