package com.leon.counter_reading.di.view_model;

import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpClientWrapper {
    public static Call call;
    public static CustomProgressModel progressBarCancelable;
    public static boolean cancel;

    public static <T> void callHttpAsync(Call<T> call, int progressType,
                                         final Context context,
                                         final ICallback<T> callback,
                                         final ICallbackIncomplete<T> callbackIncomplete,
                                         final ICallbackError callbackError) {
        cancel = false;
        CustomProgressModel progressBar = MyApplication.getApplicationComponent().CustomProgressModel();
        try {
            if (progressType == ProgressType.SHOW.getValue()) {
                progressBar.show(context, context.getString(R.string.waiting));
            } else if (progressType == ProgressType.SHOW_CANCELABLE.getValue()) {
                progressBar.show(context, context.getString(R.string.waiting), true);
            } else if (progressType == ProgressType.SHOW_CANCELABLE_REDIRECT.getValue()) {
                progressBar.show(context, context.getString(R.string.waiting), true);
            }
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }

        if (isNetworkAvailable(context)) {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (!cancel) {
                        if (progressBar.getDialog() != null)
                            try {
                                progressBar.getDialog().dismiss();
                            } catch (Exception e) {
                                new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
                            }
                        if (response.isSuccessful()) {
                            callback.execute(response);
                        } else {
                            ((Activity) context).runOnUiThread(() -> callbackIncomplete.executeIncomplete(response));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                    if (!cancel) {
                        ((Activity) context).runOnUiThread(() -> callbackError.executeError(t));
                        if (progressBar.getDialog() != null)
                            try {
                                progressBar.getDialog().dismiss();
                            } catch (Exception e) {
                                new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
                            }
                    }
                }
            });
            HttpClientWrapper.call = call;
        } else {
            if (progressBar.getDialog() != null)
                try {
                    progressBar.getDialog().dismiss();
                } catch (Exception e) {
                    new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
                }
            new CustomToast().warning(context.getString(R.string.turn_internet_on));
        }
    }

    public static <T> void callHttpAsyncProgressDismiss(Call<T> call, int progressType,
                                                        final Context context,
                                                        final ICallback<T> callback,
                                                        final ICallbackIncomplete<T> callbackIncomplete,
                                                        final ICallbackError callbackError) {

        progressBarCancelable = MyApplication.getApplicationComponent().CustomProgressModel();
        try {
            if (progressType == ProgressType.SHOW.getValue()) {
                progressBarCancelable.show(context, context.getString(R.string.waiting));
            } else if (progressType == ProgressType.SHOW_CANCELABLE.getValue()) {
                progressBarCancelable.show(context, context.getString(R.string.waiting), true);
            } else if (progressType == ProgressType.SHOW_CANCELABLE_REDIRECT.getValue()) {
                progressBarCancelable.show(context, context.getString(R.string.waiting), true);
            }
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        if (isNetworkAvailable(context)) {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (progressBarCancelable.getDialog() != null)
                        progressBarCancelable.getDialog().dismiss();
                    if (response.isSuccessful()) {
                        callback.execute(response);
                    } else {
                        ((Activity) context).runOnUiThread(() -> callbackIncomplete.executeIncomplete(response));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                    if (progressBarCancelable.getDialog() != null)
                        progressBarCancelable.getDialog().dismiss();
                    ((Activity) context).runOnUiThread(() -> callbackError.executeError(t));
                }
            });
            HttpClientWrapper.call = call;
        } else {
            if (progressBarCancelable.getDialog() != null)
                progressBarCancelable.getDialog().dismiss();
            new CustomToast().warning(context.getString(R.string.turn_internet_on));
        }
    }
}
