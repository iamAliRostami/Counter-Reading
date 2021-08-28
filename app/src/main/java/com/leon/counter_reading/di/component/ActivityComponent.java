package com.leon.counter_reading.di.component;

import com.leon.counter_reading.di.module.CustomDialogModule;
import com.leon.counter_reading.utils.custom_dialog.LovelyStandardDialog;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {CustomDialogModule.class})
public interface ActivityComponent {

    LovelyStandardDialog LovelyStandardDialog();
}
