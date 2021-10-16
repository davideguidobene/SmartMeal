package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.communication.CustomerCommunication;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.sensor.SensorDetector;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.CustomerBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.CustomerLeaveRoomAction;

public class ChooseTableFragment extends Fragment {

    private static final String TAG = "ChooseTableFragment";

    private RecyclerView tableRecyclerView;
    private TableAdapter tableAdapter;

    private Button cancelButton;

    public ChooseTableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_choose_table, container, false);

        tableRecyclerView = root.findViewById(R.id.table_recycler_view);

        joinRoom(root);

        cancelButton = root.findViewById(R.id.cancellation_button);
        cancelButton.setOnClickListener(v -> {
            CustomerCommunication.getInstance().leaveRoom();
            Intent returnIntent = new Intent();

            Activity activity = getActivity();
            if (activity != null) {
                activity.setResult(Activity.RESULT_CANCELED, returnIntent);
                activity.finish();
            }
        });
        return root;
    }

    private void joinRoom(View root) {
        CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

        Activity activity = getActivity();

        customerCommunication.onTableChanged(table -> {
            new CustomerLeaveRoomAction(activity, activity.getString(R.string.table_changed_snackbar)).run();
            customerCommunication.leaveRoom();
        });
        customerCommunication.onTableRemoved(() -> {
            new CustomerLeaveRoomAction(activity, activity.getString(R.string.table_removed_snackbar)).run();
            customerCommunication.leaveRoom();
        });

        // se il cliente non è connesso al gestore con nearby
        if (customerCommunication.isNotConnected()) {

            // imposta la callback da eseguire nel caso il gestore chiuda la stanza
            customerCommunication.onCloseRoom(() -> {
                try {
                    SensorDetector.getInstance().endShakeDetection();
                }
                catch (IllegalStateException e) {
                    Log.w(TAG, "tried to stop detection but not activated yet");
                }

                new CustomerLeaveRoomAction(activity, activity.getString(R.string.manager_closed_virtual_room)).run();
            });

            Log.i(TAG, "join room");

            if (activity != null) {
                customerCommunication.joinRoom(
                        activity,
                        () -> requestFreeTableList(root),
                        new CustomerLeaveRoomAction(activity, getString(R.string.timeout_error_snackbar))
                );
            }
        }
    }

    private void requestFreeTableList(View root) {
        CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

        customerCommunication.requestFreeTableList(
                response -> {       // callback eseguita quando arriva la risposta con la lista di tavoli dal manager
                    try {
                        TreeSet<Table> tableSet = response.getContent();

                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(() -> setupTableRecyclerView(root, tableSet));
                        }

                    } catch (TableException e) {
                        e.printStackTrace();
                    }
                },
                new CustomerLeaveRoomAction(getActivity(), getString(R.string.timeout_error_snackbar))
        );
    }

    private void setupTableRecyclerView(View root, SortedSet<Table> tableSortedSet) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        tableRecyclerView = root.findViewById(R.id.table_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
        );
        tableRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        tableAdapter = new TableAdapter(activity, tableSortedSet);
        tableRecyclerView.setAdapter(tableAdapter);
    }
}
