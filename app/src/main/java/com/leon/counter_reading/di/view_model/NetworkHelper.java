package com.leon.counter_reading.di.view_model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.counter_reading.R;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class NetworkHelper {
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final long READ_TIMEOUT = 120;
    private static final long WRITE_TIMEOUT = 60;
    private static final long CONNECT_TIMEOUT = 10;
    private static final boolean RETRY_ENABLED = false;
    private static Gson gson;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;


    public static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient
                .Builder()
                .readTimeout(READ_TIMEOUT, TIME_UNIT)
                .writeTimeout(WRITE_TIMEOUT, TIME_UNIT)
                .connectTimeout(CONNECT_TIMEOUT, TIME_UNIT)
                .retryOnConnectionFailure(RETRY_ENABLED)
                .addInterceptor(interceptor).build();
    }

    public static OkHttpClient getHttpClient(String... s) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient
                .Builder()
                .readTimeout(READ_TIMEOUT, TIME_UNIT)
                .writeTimeout(WRITE_TIMEOUT, TIME_UNIT)
                .connectTimeout(CONNECT_TIMEOUT, TIME_UNIT)
                .retryOnConnectionFailure(RETRY_ENABLED)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + s[0])
                            .build();
                    return chain.proceed(request);
                }).addInterceptor(interceptor).build();
    }

    public static OkHttpClient getHttpClient(final int denominator, String... s) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        if (denominator == 1) {
            return getHttpClient(s);
        }
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient
                    .Builder()
                    .readTimeout(READ_TIMEOUT / denominator, TIME_UNIT)
                    .writeTimeout(WRITE_TIMEOUT / denominator, TIME_UNIT)
                    .connectTimeout(CONNECT_TIMEOUT / denominator, TIME_UNIT)
                    .retryOnConnectionFailure(RETRY_ENABLED)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + s[0])
//                                .addHeader("XSRF-TOKEN", s[1])
                                .build();
                        return chain.proceed(request);
                    })
                    .addInterceptor(interceptor).build();
        }
        return okHttpClient;
    }

    public static Gson getGson() {
        if (gson == null)
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
        return gson;
    }

    public static Retrofit getInstance(boolean b, int denominator, String... s) {
        String baseUrl = b ?
                DifferentCompanyManager.getBaseUrl(DifferentCompanyManager.getActiveCompanyName()) :
                DifferentCompanyManager.getLocalBaseUrl(DifferentCompanyManager.getActiveCompanyName());
        if (s.length == 0)
            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(NetworkHelper.getHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(NetworkHelper.getHttpClient(denominator, s))
//                    .client(s[1] != null ?
//                            NetworkHelper.getHttpClient(denominator, s[0], s[1]) :
//                            NetworkHelper.getHttpClient(denominator, s[0]))
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getInstance(int denominator, String... s) {
        return getInstance(true, denominator, s);
    }

    public static Retrofit getInstance(String... s) {
        return getInstance(true, 1, s);
    }

    /**
     * with cache
     */
    public static Retrofit getInstance(Context context) {
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

