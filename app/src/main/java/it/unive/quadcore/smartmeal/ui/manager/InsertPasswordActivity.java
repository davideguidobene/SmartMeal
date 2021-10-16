package it.unive.quadcore.smartmeal.ui.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;
import it.unive.quadcore.smartmeal.ui.manager.home.ManagerHomeActivity;

// Activity per inserimento password
public class InsertPasswordActivity extends AppCompatActivity {
    private static final String TAG = "InsertPasswordAct";

    private EditText passwordEditText;
    private Button confirmationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_password);

        passwordEditText = findViewById(R.id.password_edit_text);
        confirmationButton = findViewById(R.id.confirmation_button_insert_password);

        confirmationButton.setOnClickListener(v -> { // Si vuole confermare la password

            String password = passwordEditText.getText().toString().trim();

            if (password.isEmpty() || (!ManagerStorage.checkPassword(password))) { // Password non corretta
                Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.incorrect_password_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                return;
            }

            // Password corretta

            ManagerStorage.setApplicationMode(ApplicationMode.MANAGER);

            Log.i(TAG, "Manager inserted correct password");

            // avvia l'activity principale del Gestore
            Intent intent = new Intent(InsertPasswordActivity.this, ManagerHomeActivity.class);
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