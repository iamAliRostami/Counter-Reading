package com.leon.counter_reading.utils.locating;

import static com.leon.counter_reading.MyApplication.MIN_DISTANCE_CHANGE_FOR_UPDATES;

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

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.tables.SavedLocation;
import com.leon.counter_reading.utils.CustomToast;

import org.osmdroid.config.Configuration;

public class LocationTrackerGps extends Service {
    static double latitude, longitude, accuracy;
    final Activity activity;
    boolean checkGPS = false, checkNetwork = false;
    Location location;
    LocationManager locationManager;
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

    public LocationTrackerGps(Activity activity) {
        this.activity = activity;
        Configuration.getInstance().load(activity,
                PreferenceManager.getDefaultSharedPreferences(activity));
        getLocation();
    }

    void addLocation(Location location) {
        if (location != null && (location.getLatitude() != 0 || location.getLongitude() != 0)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
            SavedLocation savedLocation = new SavedLocation(accuracy, longitude, latitude);
            MyApplication.getApplicationComponent().MyDatabase().savedLocationDao().insertSavedLocation(savedLocation);
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
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
                Location locationTemp1 = null;
                Location locationTemp2 = null;
                if (checkNetwork) {
                    if (locationManager != null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MyApplication.MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        locationTemp1 = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (checkGPS) {
                    if (locationManager != null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MyApplication.MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        locationTemp2 = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
                if (locationTemp1 == null)
                    location = locationTemp2;
                else if (locationTemp2 == null)
                    location = locationTemp1;
                else location = locationTemp1.getAccuracy() > locationTemp2.getAccuracy() ?
                            locationTemp2 : locationTemp1;
                accuracy = location.getAccuracy();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MyApplication.getApplicationComponent().SharedPreferenceModel()
                .getBoolData(SharedReferenceKeys.POINT.getValue()))
            new Handler().postDelayed(this::getLocation, MyApplication.MIN_TIME_BW_UPDATES);
        return location;
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

    public void stopListener() {
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        stopListener();
        return null;
    }

    @Override
    public void onDestroy() {
        stopListener();
        super.onDestroy();
    }
}