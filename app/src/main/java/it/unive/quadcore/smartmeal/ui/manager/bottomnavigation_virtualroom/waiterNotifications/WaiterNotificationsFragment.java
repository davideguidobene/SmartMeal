package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom.waiterNotifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;

// Fragment della stanza virtuale gestore che visualizza lista notifiche
public class WaiterNotificationsFragment extends Fragment {

    // recycler view lista notifiche
    private RecyclerView waiterNotificationRecyclerView;
    // Adapter per recycler view lista notifiche
    private WaiterNotificationsAdapter waiterNotificationsAdapter;

    private WaiterNotificationsViewModel waiterNotificationsViewModel;

    private Button reloadButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        waiterNotificationsViewModel =
                new ViewModelProvider(this).get(WaiterNotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_waiter_notifications, container, false);

        // Setta recycler view
        setupWaiterNotificationRecyclerView(root);

        reloadButton = root.findViewById(R.id.reload_button_waiter_notifications);
        reloadButton.setOnClickListener(v -> { // Aggiorna lista notifiche
            // Local.getInstance().testingUI_1(); // Testing

            waiterNotificationsAdapter.reload();
        });

        return root;
    }

    // Setto recycler view
    private void setupWaiterNotificationRecyclerView(View root) {

        waiterNotificationRecyclerView = root.findViewById(R.id.waiter_notification_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                getActivity(),
                RecyclerView.VERTICAL,
                false
        );
        waiterNotificationRecyclerView.setLayoutManager(recyclerViewLayoutManager);


        waiterNotificationsAdapter = new WaiterNotificationsAdapter(getActivity(), Local.getInstance().getWaiterNotificationList());
        waiterNotificationRecyclerView.setAdapter(waiterNotificationsAdapter);
    }

    @Override
    public void onResume() {
        waiterNotificationsAdapter.reload(); // Aggiorna lista notifiche
        super.onResume();
    }

}