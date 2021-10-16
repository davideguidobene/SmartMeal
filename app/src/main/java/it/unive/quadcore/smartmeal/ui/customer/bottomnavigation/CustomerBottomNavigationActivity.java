package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.sensor.SensorDetector;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.ui.SettingsActivity;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.CustomerVirtualRoomActivity;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.SendWelcomeNotificationCallback;
import it.unive.quadcore.smartmeal.util.PermissionHandler;

public class CustomerBottomNavigationActivity extends AppCompatActivity {

    private static final String TAG = "CustomerBottomNav";

    public static final String SHOW_SNACKBAR = "SHOW_SNACKBAR";

    private final ActivityResultLauncher<Intent> virtualRoomActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Mostra snackbar in caso di timeout nearby
                    Intent intent = result.getData();
                    Bundle bundle = intent != null ? intent.getExtras() : null;
                    if (bundle != null && bundle.getString(SHOW_SNACKBAR) != null) {
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                bundle.getString(SHOW_SNACKBAR),
                                BaseTransientBottomBar.LENGTH_LONG
                        ).show();
                    }
                }
            }
    );

    private FloatingActionButton startCustomerVirtualRoomFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bottom_navigation);

        BottomNavigationView navView = findViewById(R.id.customer_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        startCustomerVirtualRoomFab = findViewById(R.id.start_customer_virtual_room_fab);
        startCustomerVirtualRoomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionHandler.hasNearbyPermissions(CustomerBottomNavigationActivity.this)) {
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            R.string.grant_permission_snackbar_text,
                            BaseTransientBottomBar.LENGTH_LONG
                    ).show();
                    return;
                }

                virtualRoomActivityResultLauncher.launch(new Intent(
                        CustomerBottomNavigationActivity.this,
                        CustomerVirtualRoomActivity.class
                ));
            }
        });

        if (CustomerStorage.getNotificationMode()
                && PermissionHandler.hasNotificationsPermissions(this)) {
            try {
                SensorDetector
                        .getInstance()
                        .startEntranceDetection(
                                new SendWelcomeNotificationCallback(this),
                                this
                        );
            } catch (IllegalStateException e) {
                Log.w(TAG, "Entrance detection was already activated");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            startActivity(new Intent(
                    CustomerBottomNavigationActivity.this,
                    SettingsActivity.class
            ));
        }

        return super.onOptionsItemSelected(item);
    }
}
