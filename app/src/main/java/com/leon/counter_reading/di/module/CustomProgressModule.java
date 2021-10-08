package com.leon.counter_reading.di.module;


import com.leon.counter_reading.di.view_model.CustomProgressModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class CustomProgressModule {
    private final CustomProgressModel customProgressModel;

    public CustomProgressModule() {
        customProgressModel = CustomProgressModel.getInstance();
    }

    @Singleton
    @Provides
    public CustomProgressModel providesCustomProgressModel() {
        return customProgressModel;
    }
}
