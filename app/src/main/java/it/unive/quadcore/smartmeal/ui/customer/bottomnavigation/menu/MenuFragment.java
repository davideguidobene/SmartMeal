package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.model.Menu;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class MenuFragment extends Fragment {
    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        setupMenuRecyclerView(root);

        return root;
    }

    private void setupMenuRecyclerView(View root) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        menuRecyclerView = root.findViewById(R.id.menu_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
        );
        menuRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        menuAdapter = new MenuAdapter(activity, CustomerStorage.getLocalDescription().getMenu());
        menuRecyclerView.setAdapter(menuAdapter);
    }
}
