package com.leon.counter_reading.utils.updating;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.LoginActivity;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.PasswordInfo;
import com.leon.counter_reading.tables.SimpleResponse;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.di.view_model.CustomDialog;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePassword {
    public ChangePassword(Activity activity, String oldPassword, String newPassword, String newPasswordConfirm) {
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        PasswordInfo passwordInfo = new PasswordInfo(oldPassword, newPassword, newPasswordConfirm);
        Call<SimpleResponse> call = iAbfaService.changePassword(passwordInfo);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new Change(activity), new ChangeIncomplete(activity), new ChangeError(activity));
    }
}

class Change implements ICallback<SimpleResponse> {
    Activity activity;

    public Change(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(Response<SimpleResponse> response) {
        if (response.body() != null)
            new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}

class ChangeIncomplete implements ICallbackIncomplete<SimpleResponse> {
    Context context;

    public ChangeIncomplete(Context context) {
        this.context = context;
    }

    @Override
    public void executeIncomplete(Response<SimpleResponse> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        if (response.code() == 400 && response.errorBody() != null) {
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                error = jObjError.getString("message");
                new CustomToast().error(error, Toast.LENGTH_LONG);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            new CustomDialog(DialogType.Yellow, context, error,
                    context.getString(R.string.dear_user),
                    context.getString(R.string.change_password),
                    context.getString(R.string.accepted));
    }
}

class ChangeError implements ICallbackError {
    Context context;

    public ChangeError(Context context) {
        this.context = context;
    }

    @Override
    public void executeError(Throwable t) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
        String error = customErrorHandlingNew.getErrorMessageTotal(t);
        new CustomDialog(DialogType.Yellow, context, error,
                context.getString(R.string.dear_user),
                context.getString(R.string.change_password),
                context.getString(R.string.accepted));
    }
}