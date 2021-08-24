package com.leon.counter_reading.utils.locating;

import static com.leon.counter_reading.MyApplication.FASTEST_INTERVAL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.tables.SavedLocation;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;

public class LocationTrackerGoogle extends Service {
    static double latitude, longitude, accuracy;
    final Activity activity;
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    private Location location;
    final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NotNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                addLocation(location);
            }
        }
    };
    final OnSuccessListener<Location> onSuccessListener = this::addLocation;


    public LocationTrackerGoogle(Activity activity) {
        this.activity = activity;
        Configuration.getInstance().load(activity,
                PreferenceManager.getDefaultSharedPreferences(activity));
        startFusedLocation();
    }

    void addLocation(Location location) {
        if (location != null) {
            this.location = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
            SavedLocation savedLocation = new SavedLocation(accuracy, longitude, latitude);
            MyApplication.getApplicationComponent().MyDatabase().savedLocationDao().insertSavedLocation(savedLocation);
        }
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

    public Location getLocation() {
        return location;
    }

    void stopFusedLocation() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
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