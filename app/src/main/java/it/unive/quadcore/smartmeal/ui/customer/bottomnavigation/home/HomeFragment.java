package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.home;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.model.LocalDescription;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private TextView localNameTextView;
    private TextView descriptionTextView;
    private ImageView localImageView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_customer_home, container, false);

        localNameTextView = root.findViewById(R.id.local_name_text_view);
        descriptionTextView = root.findViewById(R.id.description_text_view);
        localImageView = root.findViewById(R.id.local_image_view);

        Activity activity = getActivity();
        if (activity != null) {
            if (!CustomerStorage.isInitialized()) {
                CustomerStorage.initializeStorage(activity);
            }
            LocalDescription localDescription = CustomerStorage.getLocalDescription();
            localNameTextView.setText(localDescription.getName());
            descriptionTextView.setText(localDescription.getPresentation());
            localImageView.setImageResource(R.drawable.localpicture);
        }
        return root;
    }
}
