package com.leon.counter_reading.utils;

import static com.leon.counter_reading.MyApplication.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.leon.counter_reading.MyApplication.MIN_TIME_BW_UPDATES;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.SavedLocation;

import org.jetbrains.annotations.NotNull;

public class LocationTracker extends Service implements LocationListener {

    private final Context mContext;
    protected LocationManager locationManager;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    static double latitude;
    static double longitude;
    static double accuracy;
    Location location;

    public LocationTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    private Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!checkGPS && !checkNetwork) {
                new CustomToast().warning(getString(R.string.services_is_not_available));
            } else {
                this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            accuracy = location.getAccuracy();
                            SavedLocation savedLocation = new SavedLocation(accuracy, longitude, latitude);
                            MyDatabase myDatabase = MyDatabaseClient.getInstance(mContext).getMyDatabase();
                            myDatabase.savedLocationDao().insertSavedLocation(savedLocation);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getAccuracy() {
        if (location != null)
            accuracy = location.getAccuracy();
        return accuracy;
    }

    public void stopListener() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NotNull Location location) {
        if (location.getAccuracy() != 0) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
            SavedLocation savedLocation = new SavedLocation(accuracy, longitude, latitude);
            MyDatabase myDatabase = MyDatabaseClient.getInstance(mContext).getMyDatabase();
            myDatabase.savedLocationDao().insertSavedLocation(savedLocation);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}


