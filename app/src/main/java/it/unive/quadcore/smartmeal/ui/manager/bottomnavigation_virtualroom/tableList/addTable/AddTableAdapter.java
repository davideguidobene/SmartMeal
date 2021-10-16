package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom.tableList.addTable;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.ui.manager.InformationDialogFragment;

// Adapter lista di tavoli liberi, visibile dalla schermata di aggiunta tavolo
public class AddTableAdapter extends RecyclerView.Adapter<AddTableAdapter.TableViewHolder>{
    public static final class TableViewHolder extends RecyclerView.ViewHolder {
        private TextView tableTextView;
        private Button selectButton;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tableTextView = itemView.findViewById(R.id.free_table_text_view);
            this.selectButton = itemView.findViewById(R.id.select_free_table_button);

        }
    }

    @NonNull
    private final Activity activity;
    // Lista tavoli liberi
    @NonNull
    private final List<ManagerTable> tableList;

    public AddTableAdapter(@NonNull Activity activity,@NonNull Set<ManagerTable> freeTables) {
        this.activity = activity;
        this.tableList = new ArrayList<>(freeTables);
    }

    @NonNull
    @Override
    public AddTableAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Mostro una riga
        View view = LayoutInflater.from(activity).inflate(R.layout.select_free_table_row, parent, false);
        return new AddTableAdapter.TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddTableAdapter.TableViewHolder holder, int position) {

        // Mostro una riga, nella posizione "position"

        ManagerTable table =  tableList.get(position);

        String prefix = activity.getString(R.string.table_prefix);
        holder.tableTextView.setText(String.format("%s %s", prefix, table.getId()));


        holder.selectButton.setOnClickListener(view->{ // Voglio aggiungere questo tavolo

            TextView customerTextView = activity.findViewById(R.id.insert_new_costumer_edit_text);
            String customerName = customerTextView.getText().toString().trim();

            if (customerName.isEmpty()) { // Non è stato inserito il nome cliente
                Snackbar.make(
                        activity.findViewById(android.R.id.content),
                        R.string.no_new_customer_name_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                return;
            }

            try { // Provo ad aggiungere il tavolo
                // throw new TableException(""); // Testing

                Local.getInstance().assignTable(customerName,table);


                String message = activity.getString(R.string.added_table_alert);
                new InformationDialogFragment(message)      // Dialog di conferma aggiunta tavolo
                        .show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"addedTable");

            } catch (TableException e) { // Errore nell'aggiungere questo tavolo
                String message = activity.getString(R.string.error_add_table_alert);
                new InformationDialogFragment(message)      // Dialog di notifica errore aggiunta tavolo
                        .show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"addedTable");
            }

            customerTextView.setText(""); // Azzero text view
            reload(); // Ricarico lista tavoli dell'adapter
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    // Ricarica la lista tavoli
    public void reload(){
        tableList.clear();
        tableList.addAll(Local.getInstance().getFreeTableList());
        notifyDataSetChanged();
        if(tableList.size()==0){ // Non ci sono tavoli liberi
            Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_add_table_snackbar,
                    BaseTransientBottomBar.LENGTH_INDEFINITE
            ).show();
        }
    }
}
