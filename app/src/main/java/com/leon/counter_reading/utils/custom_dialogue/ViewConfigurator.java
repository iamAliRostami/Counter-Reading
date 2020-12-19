package com.leon.counter_reading.utils.custom_dialogue;

import android.view.View;

public interface ViewConfigurator<T extends View> {
    void configureView(T v);
}
