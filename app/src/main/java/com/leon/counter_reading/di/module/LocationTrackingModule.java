package com.leon.counter_reading.di.module;

import android.app.Activity;

import com.leon.counter_reading.di.view_model.LocationTrackingGoogle;
import com.leon.counter_reading.di.view_model.LocationTrackingGps;
import com.leon.counter_reading.utils.locating.CheckSensor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class LocationTrackingModule {
    private LocationTrackingGoogle locationTrackingGoogle;
    private LocationTrackingGps locationTrackingGps;

    public LocationTrackingModule(Activity activity) {
        if (CheckSensor.checkSensor(activity,true))
            locationTrackingGoogle = LocationTrackingGoogle.getInstance(activity);
        else
            locationTrackingGps = LocationTrackingGps.getInstance(activity);
    }

    @Singleton
    @Provides
    public LocationTrackingGps providesLocationTrackingGps() {
        return locationTrackingGps;
    }


    @Singleton
    @Provides
    public LocationTrackingGoogle providesLocationTrackingGoogle() {
        return locationTrackingGoogle;
    }
}
