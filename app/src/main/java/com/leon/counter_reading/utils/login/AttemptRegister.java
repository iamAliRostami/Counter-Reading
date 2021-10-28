package com.leon.counter_reading.utils.login;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.tables.LoginFeedBack;
import com.leon.counter_reading.tables.LoginInfo;
import com.leon.counter_reading.utils.CustomToast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AttemptRegister extends AsyncTask<Activity, Activity, Void> {
    private final String username;
    private final String password;
    private final String serial;

    public AttemptRegister(String username, String password, String serial) {
        super();
        this.username = username;
        this.password = password;
        this.serial = serial;
    }

    @Override
    protected Void doInBackground(Activity... activities) {

        Retrofit retrofit = MyApplication.getApplicationComponent().NetworkHelperModel().getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<LoginFeedBack> call = iAbfaService.register(new LoginInfo(username, password, serial));
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activities[0],
                        new RegisterCompleted(),
                        new Incomplete(activities[0]),
                        new Error(activities[0])));
        return null;
    }
}

class RegisterCompleted implements ICallback<LoginFeedBack> {

    @Override
    public void execute(Response<LoginFeedBack> response) {
        String message;
        if (response.body() != null) {
            message = response.body().message;
            new CustomToast().success(message);
        }
    }
}

