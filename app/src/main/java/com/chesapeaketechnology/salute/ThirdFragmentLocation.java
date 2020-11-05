package com.chesapeaketechnology.salute;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.TextView;
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
    private static final String MAP_MARKER_POSITION_KEY = "MAP_MARKER_POSITION_KEY";
    private static final int DEFAULT_MAP_ZOOM = 18;

    private SaluteReport saluteReport;
    private View view;
    private Switch getFromMapSwitch;
    private MapView mapView;
    private GoogleMap map;
    private Marker mapMarker;

    private boolean permissionsCheckComplete = false;
    private boolean mapReady = false;

    private LatLng savedMarkerPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_third_location, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null)
        {
            savedMarkerPosition = savedInstanceState.getParcelable(MAP_MARKER_POSITION_KEY);
        }

        extractSaluteReport();

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        view.findViewById(R.id.button_next).setOnClickListener(view1 -> updateSaluteReportAndPassOn());

        getFromMapSwitch = view.findViewById(R.id.switch_get_from_map);
        final EditText editText = view.findViewById(R.id.editText);
        final TextView description = view.findViewById(R.id.description);

        // Hide/display editText/map appropriately when switch is flipped
        getFromMapSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                hideKeyboard();
                editText.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                description.setVisibility(View.GONE);
                checkAndRequestPermissions();
            } else
            {
                editText.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
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
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        mapMarker = null;
        map = null;
        mapView = null;
        mapReady = false;
        permissionsCheckComplete = false;
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Save marked location when view is temporarily destroyed (e.g. on rotation).
        if (mapMarker != null)
        {
            LatLng markedPosition = mapMarker.getPosition();
            outState.putParcelable(MAP_MARKER_POSITION_KEY, markedPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mapReady = true;

        map = googleMap;
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(false);

        setInitialMarkerLocation();

        map.setOnMapClickListener(this::setMarkerLocation);
    }

    /**
     * Helper method to set map marker position and ensure no NPEs.
     *
     * @param location LatLng to set the marker at
     */
    private void setMarkerLocation(LatLng location)
    {
        if (mapMarker == null)
        {
            mapMarker = map.addMarker(new MarkerOptions().position(location));
        } else
        {
            mapMarker.setPosition(location);
        }
    }

    /**
     * Helper method to set the marker and center the camera on it.
     *
     * @param location LatLng to set the marker at
     */
    private void setAndCenterMarker(LatLng location)
    {
        setMarkerLocation(location);
        map.moveCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(location, DEFAULT_MAP_ZOOM)));
    }

    /**
     * Get the location from the Android Location Manager and update the icon on the map.
     */
    @SuppressLint("MissingPermission")
    private void setInitialMarkerLocation()
    {
        // Make sure that the map is ready and open and that all permissions have been granted.
        // The order in which these are called can vary depending on fragment lifecycle, device rotations etc.
        if (!mapReady || !permissionsCheckComplete || mapView.getVisibility() != View.VISIBLE)
        {
            return;
        }

        // If the fragment is restoring from a saved instance state, restore the marker the user previously had
        if (savedMarkerPosition != null)
        {
            setAndCenterMarker(savedMarkerPosition);
            return;
        }

        // TODO: Docs recommend the use of FusedLocationProviderClient over LocationManager
        final LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
        {
            Log.wtf(LOG_TAG, "Location manager is null.");
            return;
        }

        // Asynchronously get the user's location and update the map.
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                setAndCenterMarker(currentPosition);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            }

            @Override
            public void onProviderEnabled(String provider)
            {
            }

            @Override
            public void onProviderDisabled(String provider)
            {
            }
        }, null);
    }

    /**
     * Called when permission checks are done.
     */
    private void onGrantedPermissions()
    {

        permissionsCheckComplete = true;
        checkLocationProvider();
        setInitialMarkerLocation();
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
                        onGrantedPermissions();
                    } else
                    {
                        Log.w(LOG_TAG, "The ACCESS_FINE_LOCATION Permission was denied.");
                    }
                }
            }
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
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_PERMISSION_REQUEST_ID);
        } else
        {
            onGrantedPermissions();
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
     * Hides the keyboard if it is open.
     */
    private void hideKeyboard()
    {
        final InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
        {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
     * Update the Salute Report with the information the user provided in this fragment's UI, and then pass it along to
     * the next step in the creation wizard.
     */
    private void updateSaluteReportAndPassOn()
    {
        final EditText locationEditText = view.findViewById(R.id.editText);
        if (getFromMapSwitch.isChecked())
        {
            if (mapMarker == null)
            {
                // A location has not been selected by the user. Don't move to the next fragment
                // and prompt the user to pick a valid location.
                Toast.makeText(getContext(), getString(R.string.select_a_location), Toast.LENGTH_LONG).show();
                return;
            }
            LatLng markedPosition = mapMarker.getPosition();
            saluteReport.setLatitude(markedPosition.latitude);
            saluteReport.setLongitude(markedPosition.longitude);
        } else
        {
            saluteReport.setLocation(locationEditText.getText().toString());
        }

        final ThirdFragmentLocationDirections.ActionLocationToUnit action =
                ThirdFragmentLocationDirections.actionLocationToUnit(saluteReport);

        NavHostFragment.findNavController(this).navigate(action);
    }
}
