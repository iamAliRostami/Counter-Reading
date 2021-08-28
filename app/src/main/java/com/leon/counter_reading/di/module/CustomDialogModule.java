package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.CustomDialog;
import com.leon.counter_reading.utils.custom_dialog.LovelyStandardDialog;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class CustomDialogModule {
    private final LovelyStandardDialog lovelyStandardDialog;

    public CustomDialogModule(Context context) {
        CustomDialog customDialog = new CustomDialog(context);
        this.lovelyStandardDialog = customDialog.getLovelyStandardDialog();
    }

    @Singleton
    @Provides
    public LovelyStandardDialog provideLovelyStandardDialog() {
        return lovelyStandardDialog;
    }
}
