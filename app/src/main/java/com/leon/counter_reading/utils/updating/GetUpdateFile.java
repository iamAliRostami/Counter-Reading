package com.leon.counter_reading.utils.updating;

import android.app.Activity;
import android.content.Context;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetUpdateFile {
    public GetUpdateFile(Activity activity) {
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<ResponseBody> call = iAbfaService.getLastApk();
        HttpClientWrapper.callHttpAsyncProgressDismiss(call, ProgressType.SHOW_CANCELABLE.getValue(),
                activity, new Update(activity), new UpdateIncomplete(activity), new UpdateError(activity));
    }
}

class Update implements ICallback<ResponseBody> {
    private final Activity activity;

    public Update(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(Response<ResponseBody> response) {
        if (!CustomFile.writeResponseApkToDisk(response.body(), activity))
            activity.runOnUiThread(() ->
                    new CustomToast().warning(activity.getString(R.string.error_update)));
    }
}

class UpdateIncomplete implements ICallbackIncomplete<ResponseBody> {
    private final Context context;

    public UpdateIncomplete(Context context) {
        this.context = context;
    }

    @Override
    public void executeIncomplete(Response<ResponseBody> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        new CustomDialogModel(DialogType.Yellow, context, error,
                context.getString(R.string.dear_user),
                context.getString(R.string.update),
                context.getString(R.string.accepted));
    }
}
