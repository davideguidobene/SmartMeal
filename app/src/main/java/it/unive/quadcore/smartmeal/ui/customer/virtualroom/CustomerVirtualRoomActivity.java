package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.communication.CustomerCommunication;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu.MenuFragment;

public class CustomerVirtualRoomActivity extends AppCompatActivity {

    private static final String TAG = "CustomerVirtualRoomAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_virtual_room);

        setFragment(new ChooseTableFragment());
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.customer_room_fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        // verifica di non essere nel menu
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.customer_room_fragment_container);
        if (currentFragment instanceof MenuFragment) {
            Log.i(TAG, "Pressed back inside MenuFragment");
            getSupportFragmentManager().popBackStack();
            return;
        }

        // mostra un Dialog di conferma
        TextView confirmTextView = new TextView(this);
        String leaveConfirmationText = getString(R.string.leave_virtual_room_dialog_text);

        confirmTextView.setText(leaveConfirmationText);
        confirmTextView.setPadding(48, 0, 48, 0);

        showLeaveRoomConfirmationDialog();
    }

    private void showLeaveRoomConfirmationDialog() {
        // mostra un Dialog di conferma
        TextView confirmTextView = new TextView(this);
        String leaveConfirmationText = getString(R.string.leave_virtual_room_dialog_text);

        confirmTextView.setText(leaveConfirmationText);
        confirmTextView.setPadding(48, 0, 48, 0);

        new AlertDialog.Builder(this)
                .setTitle(R.string.leave_virtual_dialog_title)
                .setView(confirmTextView)
                .setPositiveButton(
                        R.string.confirmation_button_text,
                        (dialog, which) -> {
                            Log.i(TAG, "Leave virtual room confirmed");
                            CustomerCommunication.getInstance().leaveRoom();

                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_CANCELED, returnIntent);
                            finish();
                        }
                )
                .setNegativeButton(
                        R.string.cancellation_button_text,
                        (dialog, which) -> dialog.cancel()
                )
                .show();
    }
}
