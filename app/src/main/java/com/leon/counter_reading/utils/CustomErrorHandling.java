package com.leon.counter_reading.utils;

import android.content.Context;

import com.leon.counter_reading.R;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class CustomErrorHandling {
    Context context;

    public CustomErrorHandling(Context context) {
        this.context = context;
    }

    public String getErrorMessageTotal(Throwable throwable) {
        String errorMessage;
        if (throwable instanceof IOException) {
            errorMessage = context.getString(R.string.error_connection);
        } else {
            errorMessage = context.getString(R.string.error_other);
        }
        return errorMessage;
    }

    public <T> String getErrorMessageDefault(Response<T> response) {
        String errorMessage;
        int code = response.code();
        if (code >= 500 && code < 600) {
            errorMessage = context.getString(R.string.error_internal);
        } else if (code == 404) {
            errorMessage = context.getString(R.string.error_change_server);
        } else if (code >= 400 && code < 500) {
            errorMessage = context.getString(R.string.error_not_auth);
        } else {
            errorMessage = context.getString(R.string.error_other);
        }
        return errorMessage;
    }

    public APIError parseError(Response<?> response) {
        try {
            Converter<ResponseBody, APIError> converter =
                    NetworkHelper.getInstance("", false).
                            responseBodyConverter(APIError.class, new Annotation[0]);
            APIError error;
            error = converter.convert(Objects.requireNonNull(response.errorBody()));
            return error;
        } catch (Exception e) {
            return new APIError();
        }
    }

    public static class APIError {
        private int errorCode;
        private String message;

        APIError() {
        }

        public int status() {
            return errorCode;
        }

        public String message() {
            return message;
        }
    }
}