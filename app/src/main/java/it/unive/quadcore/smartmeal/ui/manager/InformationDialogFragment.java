package it.unive.quadcore.smartmeal.ui.manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import it.unive.quadcore.smartmeal.R;


// Dialog generici di informazione (o informazione di errore o infromazione di successo)
public class InformationDialogFragment extends DialogFragment {

    // Messaggio del dialog
    private final String message;

    public InformationDialogFragment(String message){
        super();

        this.message=message;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String closeLabel = getActivity().getString(R.string.close_label_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setNegativeButton(closeLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

}