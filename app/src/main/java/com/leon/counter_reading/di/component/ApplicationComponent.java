package com.leon.counter_reading.di.component;

import android.content.Context;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.di.module.FlashModule;
import com.leon.counter_reading.di.view_model.FlashViewModel;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Provides;

@Singleton
@Component(modules = {FlashModule.class})
public interface ApplicationComponent {
    void inject(MyApplication myApplication);

//    @Provides
//    @Singleton
//    FlashViewModel provideFlashViewModel(FlashViewModel);

//    FlashViewModel getFlashViewModel();
    Context context();
//
//    @Provides
//    @Singleton
//    FlashViewModel flashViewModel();
//
//    @Component.Builder
//    interface Builder {
//        @BindsInstance
//        Builder context(@Named("context") Context context);
//
//        ApplicationComponent build();
//    }

}
