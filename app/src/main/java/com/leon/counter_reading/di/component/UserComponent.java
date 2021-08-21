package com.leon.counter_reading.di.component;

import android.content.Context;

import com.google.android.datatransport.runtime.dagger.Component;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.di.module.FlashModule;
import com.leon.counter_reading.di.view_model.FlashViewModel;

import javax.inject.Singleton;

@Singleton
@Component(modules = {FlashModule.class})
public interface UserComponent {
    void inject(MyApplication myApplication);

    FlashViewModel flashViewModel(Context context);

}
