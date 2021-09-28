package com.leon.counter_reading.utils.login;

import android.app.Activity;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.LoginFeedBack;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Response;

class Incomplete implements ICallbackIncomplete<LoginFeedBack> {
    private final Activity activity;

    public Incomplete(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void executeIncomplete(Response<LoginFeedBack> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        if (response.code() == 401 || response.code() == 400) {
            error = activity.getString(R.string.error_is_not_match);
            if (response.errorBody() != null) {
                try {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    error = jObjError.getString("message");
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        } /*else
            new CustomDialog(DialogType.Yellow, activity, error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.login),
                    activity.getString(R.string.accepted));*/
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}
