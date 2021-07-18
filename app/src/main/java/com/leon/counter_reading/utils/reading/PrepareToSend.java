package com.leon.counter_reading.utils.reading;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.counter_reading.MyApplication.SHOW_ERROR;

public class PrepareToSend extends AsyncTask<Activity, Integer, Integer> {
    OnOffLoadDto.OffLoadData offLoadData;
    String token;

    public PrepareToSend(String token) {
        super();
        this.token = token;
        offLoadData = new OnOffLoadDto.OffLoadData();
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        offLoadData.isFinal = false;
        offLoadData.offLoads = new ArrayList<>(MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase().onOffLoadDao().getAllOnOffLoadInsert(
                OffloadStateEnum.INSERTED.getValue(), true));
        offLoadData.offLoadReports.addAll(MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase().offLoadReportDao().
                getAllOffLoadReportByActive(true, false));
        Retrofit retrofit = NetworkHelper.getInstance(2, token);
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
        if (HttpClientWrapper.call != null)
            HttpClientWrapper.call.cancel();
        activities[0].runOnUiThread(() -> HttpClientWrapper.callHttpAsync(
                call, ProgressType.NOT_SHOW.getValue(), activities[0],
                new offLoadData(), new offLoadDataIncomplete(), new offLoadError()));
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}

class offLoadData implements ICallback<OnOffLoadDto.OffLoadResponses> {
    @Override
    public void execute(Response<OnOffLoadDto.OffLoadResponses> response) {
        if (response.body() != null && response.body().status == 200) {
            new Sent().execute(response.body());

        } else if (response.body() != null) {
            MyApplication.setErrorCounter(MyApplication.getErrorCounter() + 1);
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
            String error = customErrorHandlingNew.getErrorMessage(response.body().status);
            new CustomToast().error(error);
        }
    }
}

class offLoadError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
        if (MyApplication.getErrorCounter() < SHOW_ERROR) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomToast().error(error);
        }
        MyApplication.setErrorCounter(MyApplication.getErrorCounter() + 1);
    }
}

class offLoadDataIncomplete implements ICallbackIncomplete<OnOffLoadDto.OffLoadResponses> {
    @Override
    public void executeIncomplete(Response<OnOffLoadDto.OffLoadResponses> response) {
        if (response != null) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomToast().error(error);
        }
    }
}