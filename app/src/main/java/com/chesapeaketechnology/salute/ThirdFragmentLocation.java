package com.chesapeaketechnology.salute;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.chesapeaketechnology.salute.model.SaluteReport;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Fragment for entering location. User can either type in a location manually or flip a switch
 * to select one from a map.
 *
 * @since 0.1.0
 */
public class ThirdFragmentLocation extends Fragment implements OnMapReadyCallback
{
    private static final String LOG_TAG = ThirdFragmentLocation.class.getSimpleName();

    private SaluteReport saluteReport;
    private View view;
    private Switch getFromMapSwitch;
    private MapView mapView;
    private GoogleMap map;
    private Marker mapMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_third_location, container, false);

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        extractSaluteReport();

        view.findViewById(R.id.button_next).setOnClickListener(view1 -> updateSaluteReportAndPassOn());

        getFromMapSwitch = view.findViewById(R.id.switch_get_from_map);
        EditText editText = view.findViewById(R.id.editText);

        // Hide/display editText/map appropriately when switch is flipped
        getFromMapSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                editText.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
            } else
            {
                editText.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        // Default map marker to user's current location if available.
        Activity activity = requireActivity();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            mapMarker.setPosition(currentPosition);
            map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        });

        map.setOnMapClickListener((point) -> mapMarker.setPosition(point));
    }

    /**
     * Pull the Salute Report from the fragment's arguments and set it as an instance variable.
     */
    private void extractSaluteReport()
    {
        final Bundle arguments = getArguments();
        if (arguments == null)
        {
            Log.wtf(LOG_TAG, "The arguments bundle was null when creating the Location Fragment (Third Fragment)");
        } else
        {
            saluteReport = ThirdFragmentLocationArgs.fromBundle(arguments).getSaluteReport();
        }
    }

    /**
     * Update the Salute Report with the information the user provided in this fragment's UI, and then pass it along to
     * the next step in the creation wizard.
     */
    private void updateSaluteReportAndPassOn()
    {
        final EditText locationEditText = view.findViewById(R.id.editText);
        if (getFromMapSwitch.isChecked())
        {
            LatLng markedPosition = mapMarker.getPosition();
            saluteReport.setLatitude(markedPosition.latitude);
            saluteReport.setLongitude(markedPosition.longitude);
        } else
        {
            saluteReport.setLocation(locationEditText.getText().toString());
        }

        final ThirdFragmentLocationDirections.ActionLocationToUnit action =
                ThirdFragmentLocationDirections.actionLocationToUnit(saluteReport);

        NavHostFragment.findNavController(ThirdFragmentLocation.this).navigate(action);
    }
}
