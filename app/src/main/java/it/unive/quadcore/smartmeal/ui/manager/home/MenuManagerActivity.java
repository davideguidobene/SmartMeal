package it.unive.quadcore.smartmeal.ui.manager.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu.MenuAdapter;

// Activity che mostra menu lato gestore
public class MenuManagerActivity extends AppCompatActivity {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_manager);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_manager_menu);

        // Recycler view per la lista di prodotti
        menuRecyclerView = findViewById(R.id.menu_recycler_view_manager);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
        );
        menuRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        // Adapter per il recycler view
        menuAdapter = new MenuAdapter(this, ManagerStorage.getLocalDescription().getMenu());
        menuRecyclerView.setAdapter(menuAdapter);
    }
}