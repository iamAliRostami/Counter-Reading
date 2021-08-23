package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.FlashViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class FlashModule {
    Context context;

    public FlashModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context getContext() {
        return context;
    }

//    @Singleton
//    @Provides
//    public FlashViewModel provideFlashViewModel(FlashViewModel flashViewModel) {
//        return flashViewModel;
//    }
}
