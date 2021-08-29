package com.leon.counter_reading.di.view_model;

import static com.leon.counter_reading.MyApplication.FASTEST_INTERVAL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.infrastructure.ILocationTracker;
import com.leon.counter_reading.tables.SavedLocation;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;

public class LocationTrackerGoogleTemp extends Service implements ILocationTracker {
    private static double latitude, longitude, accuracy;
    private final Activity activity;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NotNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                addLocation(location);
            }
        }
    };
    private final OnSuccessListener<Location> onSuccessListener = this::addLocation;


    public LocationTrackerGoogleTemp(Activity activity) {
        this.activity = activity;
        Configuration.getInstance().load(activity,
                PreferenceManager.getDefaultSharedPreferences(activity));
        startFusedLocation();
    }

    void startFusedLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(MyApplication.MIN_TIME_BW_UPDATES);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        registerRequestUpdateGoogle();
    }

    void stopFusedLocation() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @SuppressLint("MissingPermission")
    void registerRequestUpdateGoogle() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, onSuccessListener);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void addLocation(Location location) {
        if (location != null) {
            this.location = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
            SavedLocation savedLocation = new SavedLocation(accuracy, longitude, latitude);
            MyApplication.getApplicationComponent().MyDatabase().savedLocationDao().insertSavedLocation(savedLocation);
            Log.e("accuracy google", String.valueOf(accuracy));
        }
    }

    @Override
    public double getAccuracy() {
        return accuracy;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public Location getLocation() {
        return location;
    }


    @Override
    public IBinder onBind(Intent intent) {
        stopFusedLocation();
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFusedLocation();
    }
}