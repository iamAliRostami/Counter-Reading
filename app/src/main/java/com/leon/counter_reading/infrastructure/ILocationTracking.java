package com.leon.counter_reading.infrastructure;

import android.location.Location;

public interface ILocationTracking {

    Location getLocation();

    double getLatitude();

    double getLongitude();

    double getAccuracy();

    void addLocation(Location location);

    Location getCurrentLocation(/*Context context*/);
}
