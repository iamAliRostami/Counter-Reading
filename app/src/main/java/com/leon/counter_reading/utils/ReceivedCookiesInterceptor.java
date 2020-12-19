package com.leon.counter_reading.utils;

import android.content.Context;
import android.util.Log;

import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;

import io.jsonwebtoken.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {

    ISharedPreferenceManager sharedPreferenceManager;

    public ReceivedCookiesInterceptor(Context context) {
        sharedPreferenceManager = new SharedPreferenceManager(context, SharedReferenceNames.ACCOUNT.getValue());
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = null;
        try {
            originalResponse = chain.proceed(chain.request());
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Log.e("error intercept", e.toString());
        }
        if (!Objects.requireNonNull(originalResponse).headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>(originalResponse.headers("Set-Cookie"));
            // Save the cookies (I saved in SharedPrefrence), you save whereever you want to save
            Log.e("cookies", cookies.toString());
        }
        return originalResponse;
    }
}
