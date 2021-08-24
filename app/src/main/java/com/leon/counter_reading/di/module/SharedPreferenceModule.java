package com.leon.counter_reading.di.module;

import android.content.Context;

import com.leon.counter_reading.di.view_model.SharedPreferencemanagerModel;
import com.leon.counter_reading.enums.SharedReferenceNames;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class SharedPreferenceModule {
    private final SharedPreferencemanagerModel sharedPreferencemanagerModel;

    public SharedPreferenceModule(Context context, SharedReferenceNames sharedReferenceNames) {
        sharedPreferencemanagerModel = new SharedPreferencemanagerModel(context, sharedReferenceNames.getValue());
    }

    @Singleton
    @Provides
    public SharedPreferencemanagerModel providesSharedPreferenceModel() {
        return sharedPreferencemanagerModel;
    }
}
