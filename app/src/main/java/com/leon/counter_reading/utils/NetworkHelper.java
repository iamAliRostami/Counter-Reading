package com.leon.counter_reading.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.counter_reading.R;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class NetworkHelper {
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final long READ_TIMEOUT = 120;
    private static final long WRITE_TIMEOUT = 60;
    private static final long CONNECT_TIMEOUT = 10;
    private static final boolean RETRY_ENABLED = true;

    private NetworkHelper() {
    }

    public static OkHttpClient getHttpClient(final String token, final String XSRF) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient
                .Builder()
                .readTimeout(READ_TIMEOUT, TIME_UNIT)
                .writeTimeout(WRITE_TIMEOUT, TIME_UNIT)
                .connectTimeout(CONNECT_TIMEOUT, TIME_UNIT)
                .retryOnConnectionFailure(RETRY_ENABLED)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("XSRF-TOKEN", XSRF)
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(interceptor).build();
    }

    public static OkHttpClient getHttpClient(final String token) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient
                .Builder()
                .readTimeout(READ_TIMEOUT, TIME_UNIT)
                .writeTimeout(WRITE_TIMEOUT, TIME_UNIT)
                .connectTimeout(CONNECT_TIMEOUT, TIME_UNIT)
                .retryOnConnectionFailure(RETRY_ENABLED)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(interceptor).build();
    }

    public static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient
                .Builder()
                .readTimeout(READ_TIMEOUT, TIME_UNIT)
                .writeTimeout(WRITE_TIMEOUT, TIME_UNIT)
                .connectTimeout(CONNECT_TIMEOUT, TIME_UNIT)
                .retryOnConnectionFailure(RETRY_ENABLED)
                .addInterceptor(interceptor).build();
    }

    public static Retrofit getInstance(String token, String XSRF, boolean b) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String baseUrl = DifferentCompanyManager.getBaseUrl(
                DifferentCompanyManager.getActiveCompanyName());
        if (!b)
            baseUrl = DifferentCompanyManager.getLocalBaseUrl(
                    DifferentCompanyManager.getActiveCompanyName());
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkHelper.getHttpClient(token, XSRF))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getInstance(String token, String XSRF) {
        return getInstance(token, XSRF, true);
    }

    public static Retrofit getInstance(String token, boolean b) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String baseUrl = DifferentCompanyManager.getBaseUrl(
                DifferentCompanyManager.getActiveCompanyName());
        if (!b)
            baseUrl = DifferentCompanyManager.getLocalBaseUrl(
                    DifferentCompanyManager.getActiveCompanyName());
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkHelper.getHttpClient(token))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getInstance(String token) {
        return getInstance(token);
    }

    public static Retrofit getInstance(boolean b) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String baseUrl = DifferentCompanyManager.getBaseUrl(
                DifferentCompanyManager.getActiveCompanyName());
        if (!b)
            baseUrl = DifferentCompanyManager.getLocalBaseUrl(
                    DifferentCompanyManager.getActiveCompanyName());
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkHelper.getHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getInstance() {
        return getInstance(true);
    }

    public static Retrofit getInstanceMap() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String baseUrl = DifferentCompanyManager.getBaseUrl(
                DifferentCompanyManager.getActiveCompanyName());
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkHelper.getHttpClient(""))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getInstanceWithCache(Context context) {
        int cacheSize = 50 * 1024 * 1024; // 50 MB
        File httpCacheDirectory = new File(context.getCacheDir(), context.getString(R.string.cache_folder));
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TIME_UNIT)
                .writeTimeout(WRITE_TIMEOUT, TIME_UNIT).connectTimeout(CONNECT_TIMEOUT, TIME_UNIT)
                .retryOnConnectionFailure(RETRY_ENABLED).addInterceptor(chain ->
                        chain.proceed(chain.request().newBuilder().build()))
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)).cache(cache).build();
        return new Retrofit.Builder()
                .baseUrl(DifferentCompanyManager.getBaseUrl(
                        DifferentCompanyManager.getActiveCompanyName()))
                .client(client).addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder().setLenient().create())).build();
    }
}

