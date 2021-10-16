package it.unive.quadcore.smartmeal.ui.manager.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;
import it.unive.quadcore.smartmeal.ui.SelectAppModeActivity;


public class ManagerSettingsFragment extends Fragment {

    private static final String TAG = "ManagerSettingsFrag";

    private TextView logoutTextView;
    private TextView aboutTextView;

    public ManagerSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_manager_settings, container, false);

        logoutTextView = root.findViewById(R.id.manager_logout_text_view);
        aboutTextView = root.findViewById(R.id.manager_about_text_view);

        logoutTextView.setOnClickListener(v -> {
            // imposta modalità applicazione predefinita
            ManagerStorage.clear();

            // ritorna alla pagina di selezione modalità
            Intent intent = new Intent(getContext(), SelectAppModeActivity.class);
            // svuota il backstack
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);
            getActivity().finish();
        });

        aboutTextView.setOnClickListener(v -> {
            TextView innerAboutTextView = new TextView(getContext());
            innerAboutTextView.setPadding(48, 0, 48, 0);
            innerAboutTextView.setText("\nSmartMeal\nDesigned by Quadcore\n");

            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.about_settings)
                    .setView(innerAboutTextView)
                    .show();
        });

        return root;
    }
}
