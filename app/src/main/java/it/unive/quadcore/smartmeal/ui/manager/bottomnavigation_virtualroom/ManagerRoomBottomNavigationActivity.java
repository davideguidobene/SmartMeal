package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unive.quadcore.smartmeal.R;

// Bottom navigation activity della stanza virtuale gestore
public class ManagerRoomBottomNavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_room_bottom_navigation);

        BottomNavigationView navView = findViewById(R.id.nav_view_manager_room);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.manager_room_navigation_waiterNotifications, R.id.manager_room_navigation_tableList)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_manager_room_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onBackPressed() { // Si vuole lasciare la stanza virtuale

        new ConfirmLeavingRoomDialogFragment().show(getSupportFragmentManager(),"confirmLeavingRoom");

    }
}