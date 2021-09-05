package com.leon.counter_reading.di.component;

import com.leon.counter_reading.di.module.CustomDialogModule;
import com.leon.counter_reading.di.module.LocationTrackingModule;
import com.leon.counter_reading.di.view_model.LocationTrackingGoogle;
import com.leon.counter_reading.di.view_model.LocationTrackingGps;
import com.leon.counter_reading.utils.custom_dialog.LovelyStandardDialog;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {CustomDialogModule.class, LocationTrackingModule.class})
public interface ActivityComponent {

    LovelyStandardDialog LovelyStandardDialog();

    LocationTrackingGps LocationTrackingGps();

    LocationTrackingGoogle LocationTrackingGoogle();

}
