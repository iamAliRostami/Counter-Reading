package com.leon.counter_reading.di.component;

import com.google.gson.Gson;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.di.module.CustomProgressModule;
import com.leon.counter_reading.di.module.FlashModule;
import com.leon.counter_reading.di.module.MyDatabaseModule;
import com.leon.counter_reading.di.module.NetworkModule;
import com.leon.counter_reading.di.module.SharedPreferenceModule;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.FlashViewModel;
import com.leon.counter_reading.di.view_model.NetworkHelperModel;
import com.leon.counter_reading.di.view_model.SharedPreferencemanagerModel;
import com.leon.counter_reading.utils.MyDatabase;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {FlashModule.class, MyDatabaseModule.class, SharedPreferenceModule.class,
        NetworkModule.class, CustomProgressModule.class})
public interface ApplicationComponent {

    void inject(MyApplication myApplication);

    FlashViewModel FlashViewModel();

    MyDatabase MyDatabase();

    SharedPreferencemanagerModel SharedPreferenceModel();

    Gson Gson();

    Retrofit Retrofit();

    NetworkHelperModel NetworkHelperModel();

    CustomProgressModel CustomProgressModel();
}
