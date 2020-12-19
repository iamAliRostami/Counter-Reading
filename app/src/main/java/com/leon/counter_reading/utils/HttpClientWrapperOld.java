package com.leon.counter_reading.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ICallbackOld;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

public class HttpClientWrapperOld {
    public static Call call;

    public static <T> void callHttpAsync(Call<T> call, int dialogType,
                                         final Context context,
                                         final ICallbackOld<T> callback,
                                         final ICallbackIncomplete<T> callbackIncomplete,
                                         final ICallbackError callbackError) {
        HttpClientWrapperOld.call = call;
        CustomProgressBar progressBar = new CustomProgressBar();
        if (dialogType == ProgressType.SHOW.getValue()) {
            progressBar.show(context, context.getString(R.string.waiting));
        } else if (dialogType == ProgressType.SHOW_CANCELABLE.getValue()) {
            progressBar.show(context, context.getString(R.string.waiting), true);
        } else if (dialogType == ProgressType.SHOW_CANCELABLE_REDIRECT.getValue()) {
            progressBar.show(context, context.getString(R.string.waiting), true);
        }
        if (isNetworkAvailable(context)) {
            HttpClientWrapperOld.call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (response.isSuccessful()) {
                        callback.execute(response.body());
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
            Toast.makeText(context, R.string.turn_internet_on, Toast.LENGTH_SHORT).show();
        }
    }
}
