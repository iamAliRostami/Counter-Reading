package com.leon.counter_reading.utils.forbid;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.di.view_model.NetworkHelper;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareForbid extends AsyncTask<Activity, Activity, Activity> {
    private final ForbiddenDto forbiddenDto;
    private final int zoneId;
    CustomProgressBar customProgressBar;

    public PrepareForbid(Activity activity, ForbiddenDto forbiddenDto, int zoneId) {
        super();
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
        this.forbiddenDto = forbiddenDto;
        this.zoneId = zoneId;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        Retrofit retrofit = NetworkHelper.getInstance(MyApplication.getApplicationComponent()
                .SharedPreferenceModel().getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<ForbiddenDto.ForbiddenDtoResponses> call;
        if (zoneId != 0 && forbiddenDto.File.size() > 0) {
            call = iAbfaService.singleForbidden(forbiddenDto.File,
                    forbiddenDto.forbiddenDtoRequest.zoneId,
                    forbiddenDto.forbiddenDtoRequest.description,
                    forbiddenDto.forbiddenDtoRequest.preEshterak,
                    forbiddenDto.forbiddenDtoRequest.nextEshterak,
                    forbiddenDto.forbiddenDtoRequest.postalCode,
                    forbiddenDto.forbiddenDtoRequest.tedadVahed,
                    forbiddenDto.forbiddenDtoRequest.x,
                    forbiddenDto.forbiddenDtoRequest.y,
                    forbiddenDto.forbiddenDtoRequest.gisAccuracy);
        } else if (zoneId == 0 && forbiddenDto.File.size() > 0) {
            call = iAbfaService.singleForbidden(forbiddenDto.File,
                    forbiddenDto.forbiddenDtoRequest.description,
                    forbiddenDto.forbiddenDtoRequest.preEshterak,
                    forbiddenDto.forbiddenDtoRequest.nextEshterak,
                    forbiddenDto.forbiddenDtoRequest.postalCode,
                    forbiddenDto.forbiddenDtoRequest.tedadVahed,
                    forbiddenDto.forbiddenDtoRequest.x,
                    forbiddenDto.forbiddenDtoRequest.y,
                    forbiddenDto.forbiddenDtoRequest.gisAccuracy);
        } else if (zoneId != 0) {
            call = iAbfaService.singleForbidden(
                    forbiddenDto.forbiddenDtoRequest.zoneId,
                    forbiddenDto.forbiddenDtoRequest.description,
                    forbiddenDto.forbiddenDtoRequest.preEshterak,
                    forbiddenDto.forbiddenDtoRequest.nextEshterak,
                    forbiddenDto.forbiddenDtoRequest.postalCode,
                    forbiddenDto.forbiddenDtoRequest.tedadVahed,
                    forbiddenDto.forbiddenDtoRequest.x,
                    forbiddenDto.forbiddenDtoRequest.y,
                    forbiddenDto.forbiddenDtoRequest.gisAccuracy);
        } else {
            call = iAbfaService.singleForbidden(
                    forbiddenDto.forbiddenDtoRequest.description,
                    forbiddenDto.forbiddenDtoRequest.preEshterak,
                    forbiddenDto.forbiddenDtoRequest.nextEshterak,
                    forbiddenDto.forbiddenDtoRequest.postalCode,
                    forbiddenDto.forbiddenDtoRequest.tedadVahed,
                    forbiddenDto.forbiddenDtoRequest.x,
                    forbiddenDto.forbiddenDtoRequest.y,
                    forbiddenDto.forbiddenDtoRequest.gisAccuracy);
        }
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activities[0],
                        new Forbidden(activities[0], forbiddenDto),
                        new ForbiddenIncomplete(activities[0], forbiddenDto),
                        new ForbiddenError(activities[0], forbiddenDto)));
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        customProgressBar.getDialog().dismiss();
    }

}

class Forbidden implements ICallback<ForbiddenDto.ForbiddenDtoResponses> {
    private final Activity activity;
    private final ForbiddenDto forbiddenDto;

    public Forbidden(Activity activity, ForbiddenDto forbiddenDto) {
        this.activity = activity;
        this.forbiddenDto = forbiddenDto;
    }

    @Override
    public void execute(Response<ForbiddenDto.ForbiddenDtoResponses> response) {
        if (!response.isSuccessful())
            MyApplication.getApplicationComponent().MyDatabase().forbiddenDao().
                    insertForbiddenDto(forbiddenDto);
        else {
            if (response.body() != null) {
                new CustomToast().success(response.body().message);
            }
        }
        activity.finish();
    }
}

class ForbiddenIncomplete implements ICallbackIncomplete<ForbiddenDto.ForbiddenDtoResponses> {
    private final Activity activity;
    private final ForbiddenDto forbiddenDto;

    public ForbiddenIncomplete(Activity activity, ForbiddenDto forbiddenDto) {
        this.activity = activity;
        this.forbiddenDto = forbiddenDto;
    }

    @Override
    public void executeIncomplete(Response<ForbiddenDto.ForbiddenDtoResponses> response) {
        MyApplication.getApplicationComponent().MyDatabase().forbiddenDao().
                insertForbiddenDto(forbiddenDto);
        activity.finish();
    }
}

class ForbiddenError implements ICallbackError {
    private final Activity activity;
    private final ForbiddenDto forbiddenDto;

    public ForbiddenError(Activity activity, ForbiddenDto forbiddenDto) {
        this.activity = activity;
        this.forbiddenDto = forbiddenDto;
    }

    @Override
    public void executeError(Throwable t) {
        MyApplication.getApplicationComponent().MyDatabase().forbiddenDao().
                insertForbiddenDto(forbiddenDto);
        activity.finish();
    }
}
