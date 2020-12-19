package com.leon.counter_reading.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

public class HttpClientWrapper {
    public static Call call;

    public static <T> void callHttpAsync(Call<T> call, int progressType,
                                         final Context context,
                                         final ICallback<T> callback,
                                         final ICallbackIncomplete<T> callbackIncomplete,
                                         final ICallbackError callbackError) {
        HttpClientWrapper.call = call;
        CustomProgressBar progressBar = new CustomProgressBar();
        if (progressType == ProgressType.SHOW.getValue()) {
            progressBar.show(context, context.getString(R.string.waiting));
        } else if (progressType == ProgressType.SHOW_CANCELABLE.getValue()) {
            progressBar.show(context, context.getString(R.string.waiting), true);
        } else if (progressType == ProgressType.SHOW_CANCELABLE_REDIRECT.getValue()) {
            progressBar.show(context, context.getString(R.string.waiting), true);
        }
        if (isNetworkAvailable(context)) {
            HttpClientWrapper.call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (response.isSuccessful()) {
                        callback.execute(response);
                    } else {
                        ((Activity) context).runOnUiThread(() -> callbackIncomplete.executeIncomplete(response));
                    }
                    if (progressBar.getDialog() != null)
                        progressBar.getDialog().dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                    ((Activity) context).runOnUiThread(() -> callbackError.executeError(t));
                    if (progressBar.getDialog() != null)
                        progressBar.getDialog().dismiss();
                }
            });
        } else {
            if (progressBar.getDialog() != null)
                progressBar.getDialog().dismiss();
            CustomToast customToast = new CustomToast();
            customToast.warning(context.getString(R.string.turn_internet_on));
        }
    }
}
