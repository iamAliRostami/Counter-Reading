package com.leon.counter_reading.infrastructure;

import android.location.Location;

public interface ILocationTracker {
    Location getLocation();

    double getLatitude();

    double getLongitude();

    double getAccuracy();

    void addLocation(Location location);
}
