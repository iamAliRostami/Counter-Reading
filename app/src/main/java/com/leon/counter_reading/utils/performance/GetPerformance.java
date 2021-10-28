package com.leon.counter_reading.utils.performance;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.fragments.ReportPerformanceFragment;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.PerformanceInfo;
import com.leon.counter_reading.tables.PerformanceResponse;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetPerformance extends AsyncTask<Activity, Activity, Activity> {
    private final CustomProgressModel customProgressModel;
    private final String startDate, endDate;
    private final ReportPerformanceFragment fragment;

    public GetPerformance(Activity activity, ReportPerformanceFragment fragment, String startDate, String endDate) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
        this.startDate = startDate;
        this.endDate = endDate;
        this.fragment = fragment;
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);

//        Call<PerformanceResponse> call = iAbfaService
//                .myPerformance(RequestBody.create(startDate, MediaType.parse("text/plain")),
//                        RequestBody.create(endDate, MediaType.parse("text/plain")));
        Call<PerformanceResponse> call = iAbfaService.myPerformance(new PerformanceInfo(startDate, endDate));
        activities[0].runOnUiThread(() -> {
            customProgressModel.getDialog().dismiss();
            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activities[0],
                    new Performance(fragment), new PerformanceIncomplete(activities[0]),
                    new PerformanceError(activities[0]));
        });
        return activities[0];
    }

}

class PerformanceError implements ICallbackError {
    private final Activity activity;

    public PerformanceError(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeError(Throwable t) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
        String error = customErrorHandlingNew.getErrorMessageTotal(t);
        new CustomToast().error(error, Toast.LENGTH_LONG);
    }
}

class PerformanceIncomplete implements ICallbackIncomplete<PerformanceResponse> {
    private final Activity activity;

    public PerformanceIncomplete(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void executeIncomplete(Response<PerformanceResponse> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}

class Performance implements ICallback<PerformanceResponse> {
    private final ReportPerformanceFragment fragment;

    public Performance(ReportPerformanceFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void execute(Response<PerformanceResponse> response) {
        fragment.setTextViewTextSetter(response.body());
    }
}
