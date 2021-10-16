package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom.tableList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.ui.manager.InformationDialogFragment;


// Dialog per la modifica di tavoli
public class ModifyTableDialogFragment extends DialogFragment {

    // Cliente di cui modificare il tavolo
    @NonNull
    private final Customer customer;
    // Adapter lista tavoli occupati (del fragment della stanza virtuale gestore di visualizzazione tavoli occupati)
    @NonNull
    private final AssignedTableListAdapter adapter;
    // Lista tavoli liberi
    @NonNull
    private final List<ManagerTable> freeTables;


   public ModifyTableDialogFragment(@NonNull Customer customer,@NonNull Set<ManagerTable> freeTables,@NonNull AssignedTableListAdapter adapter){
       super();
       this.customer = customer;
       this.adapter = adapter;
       this.freeTables = new ArrayList<>(freeTables);

   }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getActivity().getString(R.string.modify_table_alert);

        // Lista di tavoli liberi da mostrare nel dialog. Lista di stringhe
        List<String> freeTablesStrings = new ArrayList<>();
        String prefix = getActivity().getString(R.string.table_prefix);
        for(ManagerTable table : freeTables){
            freeTablesStrings.add(String.format("%s %s",prefix,table.getId()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(String.format("%s %s", title , customer.getName()))
                // Setto la lista di tavoli liberi
                .setAdapter(new ArrayAdapter<>(getContext(),R.layout.modify_table_dialog_row, freeTablesStrings),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { // Callback di selezione di un tavolo
                        try {

                           // throw new RoomStateException(true); // TEsting

                            ManagerTable newTable = freeTables.get(which);
                            Local.getInstance().changeCustomerTable(customer, newTable); // Modifico il tavolo associato al cliente

                        } catch (TableException e) { // Errore nel modificare il tavolo
                            new InformationDialogFragment(getActivity().getString(R.string.modify_table_error_alert))
                                    .show(((FragmentActivity)getContext()).getSupportFragmentManager(),"errorSelectedTable");
                        }
                        adapter.reload();
                    }
                });

        return builder.create();
    }

}
