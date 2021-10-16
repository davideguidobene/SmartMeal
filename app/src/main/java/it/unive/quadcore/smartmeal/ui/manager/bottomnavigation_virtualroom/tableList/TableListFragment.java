package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom.tableList;

import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.ui.manager.bottomnavigation_virtualroom.tableList.addTable.AddTableActivity;

// Fragment della stanza virtuale gestore che mostra lista tavoli occupati
public class TableListFragment extends Fragment {

    // Recycler view lista tavoli occupati
    private RecyclerView assignedTableListRecyclerView;
    // Adapter della recycler view lista tavoli occupati
    public AssignedTableListAdapter assignedTableListAdapter;

    private TableListViewModel tableListViewModel;

    private Button reloadButton;
    private FloatingActionButton floatingButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tableListViewModel =
                new ViewModelProvider(this).get(TableListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_table_list, container, false);

        // Setta recycler view
        setupAssignedTableListRecyclerView(root);

        reloadButton = root.findViewById(R.id.reload_button_table_list);
        reloadButton.setOnClickListener(v -> { // Ricarico la lista tavoli occupati
            // Local.getInstance().testingUI_2(); // Testing

            assignedTableListAdapter.reload();
        });

        floatingButton = root.findViewById(R.id.floating_button_add_table);
        floatingButton.setOnClickListener(v -> { // Voglio aggiungere un tavolo occupato

            if(Local.getInstance().getFreeTableList().size()==0){ // Lista tavoli liberi è vuota : non si può aggiungere un tavolo
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.error_add_table_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                return ;
            }

            // Lista tavoli liberi non è vuota : vado all'activity aggiunta tavolo

            Intent intent = new Intent(v.getContext(), AddTableActivity.class);
            startActivity(intent);

        });

        return root;
    }

    // Setto recycler view
    private void setupAssignedTableListRecyclerView(View root) {

        assignedTableListRecyclerView = root.findViewById(R.id.table_list_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(
                getActivity(),
                RecyclerView.VERTICAL,
                false
        );
        assignedTableListRecyclerView.setLayoutManager(recyclerViewLayoutManager);


        assignedTableListAdapter = new AssignedTableListAdapter(getActivity(), Local.getInstance().getAssignedTableList());
        assignedTableListRecyclerView.setAdapter(assignedTableListAdapter);
    }

    @Override
    public void onResume() {
        assignedTableListAdapter.reload(); // Aggiorna lista tavoli occupati
        super.onResume();
    }
}