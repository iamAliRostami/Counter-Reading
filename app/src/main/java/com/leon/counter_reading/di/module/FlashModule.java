package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.FlashViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class FlashModule {
    @Singleton
    @Provides
    public FlashViewModel flashViewModel(Context context) {
        return new FlashViewModel(context);
    }
}
