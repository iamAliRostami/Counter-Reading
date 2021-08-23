package com.leon.counter_reading.di.component;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.di.module.FlashModule;
import com.leon.counter_reading.di.module.MyDatabaseModule;
import com.leon.counter_reading.di.view_model.FlashViewModel;
import com.leon.counter_reading.utils.MyDatabase;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FlashModule.class, MyDatabaseModule.class})
public interface ApplicationComponent {

    void inject(MyApplication myApplication);

    FlashViewModel FlashViewModel();

    MyDatabase MyDatabase();
}
