package com.leon.counter_reading.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.tables.SavedLocation;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;

import java.util.ArrayList;

import static com.leon.counter_reading.MyApplication.FASTEST_INTERVAL;
import static com.leon.counter_reading.MyApplication.MIN_DISTANCE_CHANGE_FOR_UPDATES;

public class GPSTracker extends Service {
    final Activity activity;
    boolean canGetLocation = false;
    double latitude;
    double longitude;
    double accuracy;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    final ArrayList<SavedLocation> savedLocations = new ArrayList<>();
    Location location;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NotNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                addLocation(location);
            }
        }
    };
    final OnSuccessListener<Location> onSuccessListener = this::addLocation;
    final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (locationManager != null)
                locationManager.removeUpdates(locationListener);
            addLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public GPSTracker(Activity activity) {
        this.activity = activity;
        Configuration.getInstance().load(activity,
                PreferenceManager.getDefaultSharedPreferences(activity));
        if (checkGooglePlayServices()) {
            startFusedLocation();
        } else {
            getLocation();
        }
    }

    void addLocation(Location location) {
        if (location != null && (location.getLatitude() != 0 || location.getLongitude() != 0)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
            SavedLocation savedLocation = new SavedLocation(accuracy, longitude, latitude);
            MyDatabase myDatabase = MyDatabaseClient.getInstance(activity).getMyDatabase();
            myDatabase.savedLocationDao().insertSavedLocation(savedLocation);
//            MyDatabaseClient.getInstance(activity).destroyDatabase(myDatabase);
            savedLocations.add(savedLocation);
        }
    }

    @SuppressLint("MissingPermission")
    void getLocation() {
        try {
            locationManager = (LocationManager) activity
                    .getSystemService(LOCATION_SERVICE);
            // get GPS status
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // get network provider status
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!checkGPS && !checkNetwork) {
                new CustomToast().warning(getString(R.string.services_is_not_available));
            } else {
                this.canGetLocation = true;
                if (checkNetwork) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MyApplication.MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);//TODO
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            accuracy = location.getAccuracy();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (checkGPS && location == null) {
                    if (locationManager != null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MyApplication.MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);//TODO
                    }
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            accuracy = location.getAccuracy();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error on location", e.toString());
        }
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        if (sharedPreferenceManager.getBoolData(SharedReferenceKeys.POINT.getValue()))
            new Handler().postDelayed(this::getLocation, MyApplication.MIN_TIME_BW_UPDATES);
    }

    boolean checkGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        String message;
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                message = activity.getString(R.string.google_is_available_but_not_installed);
            } else {
                message = activity.getString(R.string.google_is_not_available);
            }
            new CustomToast().warning(message);
            return false;
        }
        return true;
    }

    void startFusedLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(MyApplication.MIN_TIME_BW_UPDATES);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        registerRequestUpdateGoogle();
    }

    @SuppressLint("MissingPermission")
    void registerRequestUpdateGoogle() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, onSuccessListener);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    void stopFusedLocation() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public void stopListener() {
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        stopFusedLocation();
        stopListener();
        return null;
    }

    @Override
    public void onDestroy() {
        stopFusedLocation();
        stopListener();
        latitude = 0;
        longitude = 0;
        super.onDestroy();
    }
}