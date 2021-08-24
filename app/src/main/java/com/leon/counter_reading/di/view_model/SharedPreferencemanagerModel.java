package com.leon.counter_reading.di.view_model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;

import javax.inject.Inject;

public class SharedPreferencemanagerModel implements ISharedPreferenceManager {
    final SharedPreferences appPrefs;

    @Inject
    public SharedPreferencemanagerModel(Context context, String xml) {
        appPrefs = context.getSharedPreferences(xml, MODE_PRIVATE);
    }

    @Override
    public boolean checkIsNotEmpty(String key) {
        if (appPrefs == null) {
            return false;
        } else if (appPrefs.getString(key, "").length() > 0) {
            return true;
        } else return !appPrefs.getString(key, "").isEmpty();
    }

    @Override
    public void putData(String key, int value) {
        SharedPreferences.Editor prefsEditor = appPrefs.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();

    }

    @Override
    public void putData(String key, String data) {
        SharedPreferences.Editor prefsEditor = appPrefs.edit();
        prefsEditor.putString(key, data);
        prefsEditor.apply();
    }

    @Override
    public void putData(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = appPrefs.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    @Override
    public String getStringData(String key) {
        return appPrefs.getString(key, "");
    }

    @Override
    public int getIntData(String key) {
        return appPrefs.getInt(key, 1);
    }

    @Override
    public boolean getBoolData(String key) {
        return appPrefs.getBoolean(key, false);
    }
}
