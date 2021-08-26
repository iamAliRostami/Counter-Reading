package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.CustomDialog;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class CustomDialogModule {
    private final CustomDialog customDialog;

    public CustomDialogModule(Context context) {
        this.customDialog = new CustomDialog(context);
    }

    @Singleton
    @Provides
    public CustomDialog providesCustomDialog() {
        return customDialog;
    }
}
