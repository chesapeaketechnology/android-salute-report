package com.chesapeaketechnology.salute;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.chesapeaketechnology.salute.model.SaluteReport;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Fragment for entering location. User can either type in a location manually or flip a switch
 * to select one from a map.
 *
 * @since 0.1.0
 */
public class ThirdFragmentLocation extends Fragment implements OnMapReadyCallback
{
    private static final String LOG_TAG = ThirdFragmentLocation.class.getSimpleName();
    private static final int ACCESS_PERMISSION_REQUEST_ID = 1;

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

    @Override
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
                hideKeyboard();
                editText.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                checkAndRequestPermissions();
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

        map.setOnMapClickListener((point) -> {
            if (mapMarker == null)
            {
                mapMarker = map.addMarker(new MarkerOptions().position(point));
                map.moveCamera(CameraUpdateFactory.newLatLng(point));
            }
            mapMarker.setPosition(point);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_PERMISSION_REQUEST_ID)
        {
            for (int index = 0; index < permissions.length; index++)
            {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[index]))
                {
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED)
                    {
                        checkLocationProvider();
                        updateMapMarkerLocation();
                    } else
                    {
                        Log.w(LOG_TAG, "The ACCESS_FINE_LOCATION Permission was denied.");
                    }
                }
            }
        }
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
     * Request the permissions needed for this app if any of them have not yet been granted.  If all of the permissions
     * are already granted then don't request anything.
     */
    private void checkAndRequestPermissions()
    {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_PERMISSION_REQUEST_ID);
        } else
        {
            checkLocationProvider();
            updateMapMarkerLocation();
        }
    }

    /**
     * Checks that the location provider is enabled.  If GPS location is not enabled on this device, then the user is
     * asked to open the settings UI is opened so it can be enabled.
     */
    private void checkLocationProvider()
    {
        final LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
        {
            Log.w(LOG_TAG, "Could not get the location manager.  Skipping checking the location provider");
            return;
        }

        final LocationProvider locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        if (locationProvider == null)
        {
            final String noGpsMessage = getString(R.string.no_gps_device);
            Log.w(LOG_TAG, noGpsMessage);
            Toast.makeText(getContext(), noGpsMessage, Toast.LENGTH_LONG).show();
        } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            // gps exists, but isn't on
            final String turnOnGpsMessage = getString(R.string.turn_on_gps);
            Log.w(LOG_TAG, turnOnGpsMessage);
            Toast.makeText(getContext(), turnOnGpsMessage, Toast.LENGTH_LONG).show();

            promptEnableGps();
        }
    }

    /**
     * Ask the user if they want to enable GPS.  If they do, then open the Location settings.
     */
    private void promptEnableGps()
    {
        new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.enable_gps_message))
                .setPositiveButton(getString(R.string.enable_gps_positive_button),
                        (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                )
                .setNegativeButton(getString(R.string.enable_gps_negative_button),
                        (dialog, which) -> {
                        }
                )
                .show();
    }

    /**
     * Get the location from the Android Location Manager and update the icon on the map.
     */
    @SuppressLint("MissingPermission")
    private void updateMapMarkerLocation()
    {
        final LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
        {
            Log.wtf(LOG_TAG, "Location manager is null.");
            return;
        }
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        map.setMyLocationEnabled(true);

        if (location != null)
        {
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

            if (mapMarker == null)
            {
                mapMarker = map.addMarker(new MarkerOptions().position(currentPosition));
                map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(currentPosition, 18)));
            } else
            {
                mapMarker.setPosition(currentPosition);
            }
        }
    }

    /**
     * Hides the keyboard if it is open.
     */
    private void hideKeyboard()
    {
        final InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
