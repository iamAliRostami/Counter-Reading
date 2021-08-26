package com.leon.counter_reading.utils.updating;

import android.app.Activity;
import android.content.Context;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.fragments.SettingUpdateFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.LastInfo;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.di.view_model.CustomDialog;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetUpdateInfo {

    public GetUpdateInfo(Activity activity, SettingUpdateFragment settingUpdateFragment) {
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<LastInfo> call = iAbfaService.getLastInfo();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new UpdateInfo(settingUpdateFragment), new UpdateInfoIncomplete(activity), new UpdateError(activity));
    }
}

class UpdateInfo implements ICallback<LastInfo> {
    SettingUpdateFragment settingUpdateFragment;

    public UpdateInfo(SettingUpdateFragment settingUpdateFragment) {
        this.settingUpdateFragment = settingUpdateFragment;
    }

    @Override
    public void execute(Response<LastInfo> response) {
        if (response.body() != null) {
            settingUpdateFragment.updateInfoUi(response.body());
        }
    }
}

class UpdateInfoIncomplete implements ICallbackIncomplete<LastInfo> {
    Context context;

    public UpdateInfoIncomplete(Context context) {
        this.context = context;
    }

    @Override
    public void executeIncomplete(Response<LastInfo> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        new CustomDialog(DialogType.Yellow, context, error,
                context.getString(R.string.dear_user),
                context.getString(R.string.update),
                context.getString(R.string.accepted));
    }
}
