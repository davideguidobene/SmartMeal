package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.maps;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.model.LocalDescription;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final float DEFAULT_MAP_ZOOM_LEVEL = 15;

        LocalDescription localDescription = CustomerStorage.getLocalDescription();
        Location location = localDescription.getLocation();

        LatLng localLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(localLatLng)
                .title(localDescription.getName()));

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_MAP_ZOOM_LEVEL));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(localLatLng));
    }
}
