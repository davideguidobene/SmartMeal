package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.ui.manager.home.ManagerHomeActivity;


// Dialog di uscita dalla stanza virtuale
public class ConfirmLeavingRoomDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String cancelLabel = getString(R.string.cancel_label_alert);
        String confirmLabel = getString(R.string.confirm_label_alert);
        String message = getString(R.string.confirm_close_room_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Context context = this.getContext();
        builder.setMessage(message)
                .setPositiveButton(confirmLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Local.getInstance().closeRoom();
                        Intent intent = new Intent(getActivity(), ManagerHomeActivity.class);

                        // Disabilita WI-FI all'uscita della stanza virtuale
                        WifiManager wifi = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifi.setWifiEnabled(false);
                        // Disabilita Bluetooth all'uscita della stanza virtuale
                        BluetoothManager bt =(BluetoothManager)context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
                        // Se il dispositivo possiede il bluetooth
                        if(bt.getAdapter() != null)
                            bt.getAdapter().disable();

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Non si può tornare indietro
                        startActivity(intent);
                    }
                })
        .setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}
