package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.FlashViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class FlashModule {
    private final FlashViewModel flashViewModel;

    public FlashModule(Context context) {
        this.flashViewModel = new FlashViewModel(context);
    }

    @Singleton
    @Provides
    public FlashViewModel providesFlashViewModel() {
        return flashViewModel;
    }
}
