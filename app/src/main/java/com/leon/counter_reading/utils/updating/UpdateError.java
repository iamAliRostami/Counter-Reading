package com.leon.counter_reading.utils.updating;

import android.content.Context;

import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.custom_dialogue.CustomDialog;

public class UpdateError implements ICallbackError {
    Context context;

    public UpdateError(Context context) {
        this.context = context;
    }

    @Override
    public void executeError(Throwable t) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
        String error = customErrorHandlingNew.getErrorMessageTotal(t);
        new CustomDialog(DialogType.Red, context, error,
                context.getString(R.string.dear_user),
                context.getString(R.string.update),
                context.getString(R.string.accepted));
    }
}