package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.Constants.GPS_CODE;
import static com.leon.counter_reading.helpers.Constants.REQUEST_NETWORK_CODE;
import static com.leon.counter_reading.helpers.Constants.REQUEST_WIFI_CODE;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityLocationBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.SavedLocation;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.PermissionManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class LocationActivity extends BaseActivity {
    private static ArrayList<SavedLocation.LocationOnMap> savedLocations;
    private ActivityLocationBinding binding;
    private Activity activity;
    private ISharedPreferenceManager sharedPreferenceManager;
    private ShowOnMap showOnMap;
    private ArrayList<Marker> markers = new ArrayList<>();

    @Override
    protected void initialize() {
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        showOnMap = new ShowOnMap();
        if (isNetworkAvailable(getApplicationContext()))
            checkPermissions();
        else PermissionManager.enableNetwork(this);
    }

    void initializeCheckBoxPoint() {
        binding.checkBoxPoint.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.POINT.getValue()));
        binding.checkBoxPoint.setOnClickListener(v -> sharedPreferenceManager.putData(SharedReferenceKeys.POINT.getValue(), binding.checkBoxPoint.isChecked()));
        binding.checkBoxShowPoint.setOnClickListener(v -> {
            if (binding.checkBoxShowPoint.isChecked()) {
                showOnMap = new ShowOnMap();
                showOnMap.execute();
            } else {
                clearMap();
            }
        });
    }

    void clearMap() {
        showOnMap.cancel(true);
        binding.mapView.getOverlayManager().removeAll(markers);
        markers.clear();
    }

    void checkPermissions() {
        if (PermissionManager.gpsEnabled(this))
            if (PermissionManager.checkLocationPermission(getApplicationContext())) {
                askLocationPermission();
            } else if (PermissionManager.checkStoragePermission(getApplicationContext())) {
                askStoragePermission();
            } else {
                sharedPreferenceManager = MyApplication.getApplicationComponent().SharedPreferenceModel();
                initializeMapView();
                initializeCheckBoxPoint();
            }
    }

    void askStoragePermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).check();
    }

    void askLocationPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).check();
    }

    void initializeMapView() {
        binding.mapView.getZoomController().
                setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        binding.mapView.setMultiTouchControls(true);
        IMapController mapController = binding.mapView.getController();
        mapController.setZoom(19.0);
        if (getLocationTracker(activity).getCurrentLocation() != null) {
            GeoPoint startPoint = new GeoPoint(getLocationTracker(activity).getCurrentLocation().getLatitude(),
                    getLocationTracker(activity).getCurrentLocation().getLongitude());
            mapController.setCenter(startPoint);
        }
        MyLocationNewOverlay locationOverlay =
                new MyLocationNewOverlay(new GpsMyLocationProvider(activity), binding.mapView);
        locationOverlay.enableMyLocation();
        binding.mapView.getOverlays().add(locationOverlay);
        new GetDBLocation().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == GPS_CODE)
                checkPermissions();
            if (requestCode == REQUEST_NETWORK_CODE) {
                if (isNetworkAvailable(getApplicationContext()))
                    checkPermissions();
                else PermissionManager.setMobileWifiEnabled(this);
            }
            if (requestCode == REQUEST_WIFI_CODE) {
                if (isNetworkAvailable(getApplicationContext()))
                    checkPermissions();
                else PermissionManager.enableNetwork(this);
            }
        }
    }

    @Override
    protected void onStop() {
        if (showOnMap != null)
            showOnMap.cancel(true);
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        clearMap();
        savedLocations = null;
        binding = null;
        markers = null;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }

    static class GetDBLocation extends AsyncTask<Void, Void, Void> {

        public GetDBLocation() {
            super();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                savedLocations = new ArrayList<>(MyApplication.getApplicationComponent().MyDatabase()
                        .savedLocationDao().getSavedLocationsXY());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("size", String.valueOf(savedLocations.size()));
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ShowOnMap extends AsyncTask<Void, Void, Void> {
        public ShowOnMap() {
            super();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (savedLocations == null || savedLocations.isEmpty()) {
                savedLocations = new ArrayList<>(
                        MyApplication.getApplicationComponent().MyDatabase().
                                savedLocationDao().getSavedLocationsXY());
            }
            markers = new ArrayList<>();
            int i = 0;
            while (i < savedLocations.size() && !isCancelled()) {
                addPlace(new GeoPoint(savedLocations.get(i).latitude, savedLocations.get(i).longitude));
                i++;
            }
            return null;
        }

        void addPlace(GeoPoint p) {
            try {
                GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
                Marker marker = new Marker(binding.mapView);
                marker.setPosition(startPoint);
                marker.setIcon(ContextCompat.getDrawable(activity, R.drawable.img_marker));
                markers.add(marker);
                binding.mapView.getOverlayManager().add(marker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}