package com.leon.counter_reading.utils.login;

import android.app.Activity;

import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.custom_dialogue.CustomDialog;

class Error implements ICallbackError {
    private final Activity activity;

    public Error(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeError(Throwable t) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
        String error = customErrorHandlingNew.getErrorMessageTotal(t);
        new CustomDialog(DialogType.Yellow, activity, error,
                activity.getString(R.string.dear_user),
                activity.getString(R.string.login),
                activity.getString(R.string.accepted));
    }
}
