package com.leon.counter_reading.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Debug;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityLocationBinding;
import com.leon.counter_reading.fragments.LocationFragment;
import com.leon.counter_reading.fragments.PlaceFragment;
import com.leon.counter_reading.tables.SavedLocation;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.GPSTracker;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.PermissionManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

public class LocationActivity extends BaseActivity {
    ActivityLocationBinding binding;
    int polygonIndex, startIndex = 0;
    ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    ArrayList<SavedLocation> savedLocations = new ArrayList<>();
    Polyline line;
    Activity activity;

    @Override
    protected void initialize() {
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        if (isNetworkAvailable(getApplicationContext()))
            checkPermissions();
        else PermissionManager.enableNetwork(this);
    }

    void checkPermissions() {
        if (PermissionManager.gpsEnabled(this))
            if (PermissionManager.checkLocationPermission(getApplicationContext())) {
                askLocationPermission();
            } else if (PermissionManager.checkStoragePermission(getApplicationContext())) {
                askStoragePermission();
            } else {
                initializeMapView();
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
        GPSTracker gpsTracker = new GPSTracker(activity);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        if (latitude == 0 || longitude == 0) {
            latitude = 32.65;
            longitude = 51.66;
        }
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);
        MyLocationNewOverlay locationOverlay =
                new MyLocationNewOverlay(new GpsMyLocationProvider(activity), binding.mapView);
        locationOverlay.enableMyLocation();
        binding.mapView.getOverlays().add(locationOverlay);
        new GetDBLocation().execute();
    }

    @SuppressLint("StaticFieldLeak")
    class GetDBLocation extends AsyncTask<Integer, Integer, Integer> {
        public GetDBLocation() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            int total = MyDatabaseClient.getInstance(activity).getMyDatabase().savedLocationDao().
                    getSavedLocationsCount();
            line = new Polyline(binding.mapView);
            line.getOutlinePaint().setColor(Color.YELLOW);
            for (int i = 1; i <= total; i = i + 10) {
                savedLocations.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                        savedLocationDao().getSavedLocations(i, i + 9));
                onProgressUpdate(savedLocations.size());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            savedLocations.clear();
            polygonPoint.clear();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            for (int i = startIndex; i < values[0]; i++) {
                addPlace(new GeoPoint(savedLocations.get(i).latitude, savedLocations.get(i).longitude));
//                createPolygon(new GeoPoint(savedLocations.get(i).latitude, savedLocations.get(i).longitude));
            }
            startIndex = values[0];
            super.onProgressUpdate(values);
        }

        void addPlace(GeoPoint p) {
            GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
            Marker startMarker = new Marker(binding.mapView);
            startMarker.setPosition(startPoint);
//            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            startMarker.setIcon(ContextCompat.getDrawable(activity, R.drawable.img_marker));
            binding.mapView.getOverlayManager().add(startMarker);
        }

        void createPolygon(GeoPoint geoPoint) {
            if (polygonIndex != 0) {
                try {
                    binding.mapView.getOverlays().remove(polygonIndex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                binding.mapView.getOverlays().add(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
            polygonPoint.add(geoPoint);
            polygonPoint.add(polygonPoint.get(0));
            line.setPoints(polygonPoint);
            polygonPoint.remove(polygonPoint.size() - 1);
            polygonIndex = binding.mapView.getOverlays().size() - 1;
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == MyApplication.GPS_CODE)
                checkPermissions();
            if (requestCode == MyApplication.REQUEST_NETWORK_CODE) {
                if (isNetworkAvailable(getApplicationContext()))
                    checkPermissions();
                else PermissionManager.setMobileWifiEnabled(this);
            }
            if (requestCode == MyApplication.REQUEST_WIFI_CODE) {
                if (isNetworkAvailable(getApplicationContext()))
                    checkPermissions();
                else PermissionManager.enableNetwork(this);
            }
        }
    }

    @Override
    protected void onStop() {
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
        polygonPoint = null;
        savedLocations = null;
        line = null;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }
}