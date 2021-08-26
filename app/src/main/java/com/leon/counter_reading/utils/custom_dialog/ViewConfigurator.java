package com.leon.counter_reading.utils.custom_dialog;

import android.view.View;

public interface ViewConfigurator<T extends View> {
    void configureView(T v);
}
