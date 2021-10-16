package it.unive.quadcore.smartmeal.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.Storage;
import it.unive.quadcore.smartmeal.ui.customer.CustomerSettingsFragment;
import it.unive.quadcore.smartmeal.ui.manager.home.ManagerSettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // mostra il Fragment relativo al cliente o al gestore a seconda dell'ApplicationMode
        Fragment settingsFragment = null;
        switch (Storage.getApplicationMode()) {
            case CUSTOMER:
                settingsFragment = new CustomerSettingsFragment();
                break;
            case MANAGER:
                settingsFragment = new ManagerSettingsFragment();
                break;
            default:
                Log.wtf(TAG, "ApplicationMode is neither CUSTOMER nor MANAGER");
                return;
        }
        setFragment(settingsFragment);
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.settings_fragment_container, fragment)
                .commit();
    }
}
