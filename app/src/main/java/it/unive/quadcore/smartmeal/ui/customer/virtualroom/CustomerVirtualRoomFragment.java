package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.communication.CustomerCommunication;
import it.unive.quadcore.smartmeal.sensor.SensorDetector;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu.MenuFragment;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.CustomerLeaveRoomAction;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.NotifyWaiterCallback;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.NotifyWaiterConfirmationCallback;
import it.unive.quadcore.smartmeal.util.PermissionHandler;

public class CustomerVirtualRoomFragment extends Fragment {

    private static final String TAG = "CustomerVirtualRoomFrag";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TABLE_ID_PARAM = "tableId";


    private String tableId;


    private TextView tableNumberTextView;
    private Button menuButton;
    private Button callButton;
    private Button exitButton;


    public CustomerVirtualRoomFragment() {
        // Required empty public constructor

        this.tableId = "";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tableId Parameter 1.
     * @return A new instance of fragment CustomerVirtualRoomFragment.
     */
    public static CustomerVirtualRoomFragment newInstance(String tableId) {
        CustomerVirtualRoomFragment fragment = new CustomerVirtualRoomFragment();
        Bundle args = new Bundle();
        args.putString(TABLE_ID_PARAM, tableId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tableId = getArguments().getString(TABLE_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_customer_virtual_room, container, false);

        tableNumberTextView = root.findViewById(R.id.table_number_text_view);
        menuButton = root.findViewById(R.id.menu_button);
        callButton = root.findViewById(R.id.call_button);
        exitButton = root.findViewById(R.id.exit_customer_room_button);


        tableNumberTextView.setText(tableId);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    return;
                }

                MenuFragment menuFragment = new MenuFragment();

                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.customer_room_fragment_container, menuFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerCommunication customerCommunication = CustomerCommunication.getInstance();

                customerCommunication.notifyWaiter(
                        new NotifyWaiterConfirmationCallback(getActivity()),
                        new CustomerLeaveRoomAction(getActivity(), getString(R.string.timeout_error_snackbar))
                );
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaveRoomConfirmationDialog();

            }
        });

        CustomerCommunication.getInstance().onTableChanged(table -> tableNumberTextView.setText(table.getId()));

        if (CustomerStorage.getSensorMode() && PermissionHandler.hasSensorsPermissions(getContext())) {
            try {
                SensorDetector.getInstance().startShakeDetection(new NotifyWaiterCallback(getActivity()), getActivity());
            } catch (IllegalStateException e) {
                Log.w(TAG, "Shake detection was already activated");
            }
        }

        return root;
    }

    private void showLeaveRoomConfirmationDialog() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // mostra un Dialog di conferma
        TextView confirmTextView = new TextView(getContext());
        String leaveConfirmationText = getString(R.string.leave_virtual_room_dialog_text);

        confirmTextView.setText(leaveConfirmationText);
        confirmTextView.setPadding(48, 0, 48, 0);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.leave_virtual_dialog_title)
                .setView(confirmTextView)
                .setPositiveButton(
                        R.string.confirmation_button_text,
                        (dialog, which) -> {
                            Log.i(TAG, "Leave virtual room confirmed");
                            CustomerCommunication.getInstance().leaveRoom();

                            Intent returnIntent = new Intent();
                            activity.setResult(Activity.RESULT_CANCELED, returnIntent);
                            activity.finish();
                        }
                )
                .setNegativeButton(
                        R.string.cancellation_button_text,
                        (dialog, which) -> dialog.cancel()
                )
                .show();
    }
}
