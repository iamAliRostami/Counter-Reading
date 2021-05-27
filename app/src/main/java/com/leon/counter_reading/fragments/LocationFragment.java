package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.databinding.FragmentLocationBinding;
import com.leon.counter_reading.tables.SavedLocation;
import com.leon.counter_reading.utils.GPSTracker;
import com.leon.counter_reading.utils.MyDatabaseClient;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class LocationFragment extends Fragment {
    FragmentLocationBinding binding;
    Context context;
    int polygonIndex, startIndex = 0;
    ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    ArrayList<SavedLocation> savedLocations = new ArrayList<>();
    GetDBLocation getDBLocation;
    Polyline line;

    public LocationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Configuration.getInstance().load(context,
                PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        initializeMap();
    }

    @SuppressLint("MissingPermission")
    void initializeMap() {
        binding.mapView.getZoomController().
                setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        binding.mapView.setMultiTouchControls(true);
        IMapController mapController = binding.mapView.getController();
        mapController.setZoom(19.0);
        GPSTracker gpsTracker = new GPSTracker(getActivity());

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        if (latitude == 0 || longitude == 0) {
            latitude = 32.65;
            longitude = 51.66;
        }
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);
        MyLocationNewOverlay locationOverlay =
                new MyLocationNewOverlay(new GpsMyLocationProvider(context), binding.mapView);
        locationOverlay.enableMyLocation();
        binding.mapView.getOverlays().add(locationOverlay);
        getDBLocation = new GetDBLocation();
        getDBLocation.execute();
    }

    @SuppressLint("StaticFieldLeak")
    class GetDBLocation extends AsyncTask<Integer, Integer, Integer> {
        public GetDBLocation() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            int total = MyDatabaseClient.getInstance(context).getMyDatabase().savedLocationDao().
                    getSavedLocationsCount();
            line = new Polyline(binding.mapView);
            line.getOutlinePaint().setColor(Color.YELLOW);
            for (int i = 1; i <= total; i = i + 10) {
                savedLocations.addAll(MyDatabaseClient.getInstance(context).getMyDatabase().
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
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            binding.mapView.getOverlayManager().add(startMarker);
        }

        //        void createPolygon(GeoPoint geoPoint) {
//            try {
//                binding.mapView.getOverlays().add(line);
//                polygonPoint.add(geoPoint);
//                line.setPoints(polygonPoint);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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
    public void onDestroy() {
        super.onDestroy();
        getDBLocation = null;
        polygonPoint = null;
        savedLocations = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        getDBLocation.cancel(true);
    }
}