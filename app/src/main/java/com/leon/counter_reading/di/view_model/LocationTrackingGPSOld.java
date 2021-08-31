package com.leon.counter_reading.di.view_model;


import static com.leon.counter_reading.MyApplication.MIN_DISTANCE_CHANGE_FOR_UPDATES;
import static com.leon.counter_reading.MyApplication.MIN_TIME_BW_UPDATES;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.ILocationTracking;
import com.leon.counter_reading.tables.SavedLocation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocationTrackingGPSOld implements ILocationTracking {
    private static LocationTrackingGPSOld instance = null;
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static boolean isRegistered = false;
    private volatile static Location location;

    public static synchronized LocationTrackingGPSOld getInstance(Context context) {
        if (instance == null) {
            instance = new LocationTrackingGPSOld();
            registerLocationListeners(context);
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    private synchronized static void registerLocationListeners(Context context) {
        Log.e("time", String.valueOf(Calendar.getInstance().getTime()));

        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        Criteria criteria = new Criteria();
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        else
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        if (locationListener == null)
            createLocationListeners();
        try {
            final String bestProvider = locationManager.getBestProvider(criteria, true);
//            if (bestProvider != null)
            locationManager.requestLocationUpdates(bestProvider, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
//            else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
//            else
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            isRegistered = true;
        } catch (Exception e) {
            e.printStackTrace();
            isRegistered = false;
        }
    }

    private synchronized static void removeLocationListeners() {
        isRegistered = false;
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        }
    }

    private synchronized static void createLocationListeners() {
        locationListener = new LocationListener() {
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

            public void onLocationChanged(Location location) {
                instance.addLocation(location);
            }
        };
    }

    public synchronized boolean isRegistered() {
        return isRegistered;
    }

    public synchronized boolean hasLocation() {
        return location != null;
    }

    @Override
    @SuppressLint("MissingPermission")
    public Location getCurrentLocation(Context context) {
        if (!isRegistered()) return getBestLastKnownLocation(context);
        Location bestLocation = null;
        try {
            Criteria criteria = new Criteria();
            List<String> providers = locationManager.getProviders(criteria, false);
            for (String provider : providers) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    if (bestLocation == null) {
                        bestLocation = location;
                    } else {
                        if (location.getTime() > bestLocation.getTime())
                            bestLocation = location;
                    }
                }
            }
            if (bestLocation == null) {
                bestLocation = getBestLastKnownLocation(context);
            }
            instance.addLocation(bestLocation);
            return bestLocation;
        } catch (Exception e) {
            e.printStackTrace();
            removeLocationListeners();
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    private static Location getBestLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        Location bestLocation = null;
        List<String> providers = locationManager.getProviders(criteria, false);
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                if (bestLocation == null) {
                    bestLocation = location;
                } else {
                    if (location.getTime() > bestLocation.getTime())
                        bestLocation = location;
                }
            }
        }
        return bestLocation;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public double getLatitude() {
        return location.getLatitude();
    }

    @Override
    public double getLongitude() {
        return location.getLongitude();
    }

    @Override
    public double getAccuracy() {
        return location.getAccuracy();
    }

    @Override
    public void addLocation(Location location) {
        if (location != null && (location.getLatitude() != 0 || location.getLongitude() != 0)) {
            LocationTrackingGPSOld.location = location;
            SavedLocation savedLocation = new SavedLocation(location.getAccuracy(),
                    location.getLongitude(), location.getLatitude());
            if (MyApplication.getApplicationComponent().SharedPreferenceModel().getBoolData(SharedReferenceKeys.POINT.getValue()))
                MyApplication.getApplicationComponent().MyDatabase().savedLocationDao()
                        .insertSavedLocation(savedLocation);
            Log.e("accuracy 2", " updated: " + new Date(location.getTime()) + " accuracy: " + location.getAccuracy());
        }
    }

}