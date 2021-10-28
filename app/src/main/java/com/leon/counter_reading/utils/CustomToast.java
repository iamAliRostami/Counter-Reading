package com.leon.counter_reading.utils;

import android.widget.Toast;

import com.leon.counter_reading.helpers.MyApplication;

import es.dmoral.toasty.Toasty;

public class CustomToast {
    public void error(String s) {
        error(s, Toast.LENGTH_SHORT);
    }

    public void error(String s, int duration) {
        Toasty.error(MyApplication.getContext(), s, duration, true).show();
    }

    public void success(String s) {
        success(s, Toast.LENGTH_SHORT);
    }

    public void success(String s, int duration) {
        Toasty.success(MyApplication.getContext(), s, duration, true).show();
    }

    public void info(String s) {
        info(s, Toast.LENGTH_SHORT);
    }

    public void info(String s, int duration) {
        Toasty.info(MyApplication.getContext(), s, duration, true).show();
    }

    public void warning(String s) {
        warning(s, Toast.LENGTH_SHORT);
    }

    public void warning(String s, int duration) {
        Toasty.warning(MyApplication.getContext(), s, duration, true).show();
    }

    public void normal(String s, int drawable) {
        Toasty.normal(MyApplication.getContext(), s, drawable).show();
    }

    public void custom(String s, int drawable, int duration, int tintColor,
                       boolean withIcon, boolean shouldTint) {
        Toasty.custom(MyApplication.getContext(), s, drawable, tintColor, duration, withIcon,
                shouldTint).show();
    }
}
