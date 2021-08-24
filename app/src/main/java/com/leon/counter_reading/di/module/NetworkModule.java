package com.leon.counter_reading.di.module;

import com.google.gson.Gson;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.di.view_model.NetworkHelperModel;
import com.leon.counter_reading.enums.SharedReferenceKeys;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Singleton
@Module
public class NetworkModule {
    NetworkHelperModel networkHelperModel;

    public NetworkModule() {
        networkHelperModel = new NetworkHelperModel();
    }

    @Singleton
    @Provides
    public Gson providesGson() {
        return networkHelperModel.getGson();
    }

    @Singleton
    @Provides
    public Retrofit providesRetrofit() {
        return networkHelperModel.getInstance(MyApplication.getApplicationComponent()
                .SharedPreferenceModel().getStringData(SharedReferenceKeys.TOKEN.getValue()));
    }

    @Singleton
    @Provides
    public NetworkHelperModel providesNetworkHelperModel() {
        return networkHelperModel;
    }
}
