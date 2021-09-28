package com.leon.counter_reading.utils.locating;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.leon.counter_reading.R;
import com.leon.counter_reading.utils.CustomToast;

import org.osmdroid.config.Configuration;

public class CheckSensor {

    public static boolean checkSensor(Context context, boolean showMessage) {
        Configuration.getInstance().load(context,
                PreferenceManager.getDefaultSharedPreferences(context));
        return checkGooglePlayServices(context, showMessage);
    }

    public static boolean checkGooglePlayServices(Context context, boolean showMessage) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        String message;
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                message = context.getString(R.string.google_is_available_but_not_installed);
            } else {
                message = context.getString(R.string.google_is_not_available);
            }
            if (showMessage)
                new CustomToast().warning(message);
            return false;
        }
        return true;
    }
}
