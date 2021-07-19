package com.leon.counter_reading.utils.uploading;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareOffLoadToUpload extends AsyncTask<Activity, Activity, Activity> {
    CustomProgressBar customProgressBar;
    ISharedPreferenceManager sharedPreferenceManager;
    ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
    ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();
    int trackNumber;
    String id;

    public PrepareOffLoadToUpload(Activity activity, int trackNumber, String id) {
        super();
        this.trackNumber = trackNumber;
        this.id = id;
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
        sharedPreferenceManager = new SharedPreferenceManager(activity,
                SharedReferenceNames.ACCOUNT.getValue());
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        forbiddenDtos.clear();
        forbiddenDtos.addAll(MyDatabaseClient.getInstance(activities[0]).getMyDatabase().
                forbiddenDao().getAllForbiddenDto(false));
        onOffLoadDtos.clear();
        onOffLoadDtos.addAll(MyDatabaseClient.getInstance(activities[0]).getMyDatabase().
                onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(trackNumber,
                OffloadStateEnum.INSERTED.getValue()));
        offLoadReports.clear();
        offLoadReports.addAll(MyDatabaseClient.getInstance(activities[0]).getMyDatabase().
                offLoadReportDao().getAllOffLoadReport(false));
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        customProgressBar.getDialog().dismiss();
        uploadOffLoad(activity);
        if (forbiddenDtos.size() > 0) {
            uploadForbid(activity);
        }
    }

    void uploadForbid(Activity activity) {
        ForbiddenDto.ForbiddenDtoRequestMultiple forbiddenDtoRequestMultiple =
                new ForbiddenDto.ForbiddenDtoRequestMultiple();
        Retrofit retrofit = NetworkHelper.getInstance(
                sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        for (ForbiddenDto forbiddenDto : forbiddenDtos) {
            ForbiddenDto.ForbiddenDtoMultiple forbiddenDtoMultiple =
                    new ForbiddenDto.ForbiddenDtoMultiple(forbiddenDto.zoneId,
                            forbiddenDto.description, forbiddenDto.preEshterak,
                            forbiddenDto.nextEshterak, forbiddenDto.postalCode,
                            forbiddenDto.tedadVahed, forbiddenDto.x, forbiddenDto.y,
                            forbiddenDto.gisAccuracy);
            forbiddenDtoRequestMultiple.forbiddenDtos.add(forbiddenDtoMultiple);
        }
        Call<ForbiddenDto.ForbiddenDtoResponses> call =
                iAbfaService.multipleForbidden(forbiddenDtoRequestMultiple);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new Forbidden(), new ForbiddenIncomplete(), new UploadForbidenError());
    }

    void uploadOffLoad(Activity activity) {
        if (onOffLoadDtos.size() <= 0) {
            thankYou(activity);
            onOffLoadDtos.clear();
            onOffLoadDtos.add(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(trackNumber));
        }
        if (onOffLoadDtos.size() == 0 || onOffLoadDtos.get(0) == null) {
            MyDatabaseClient.getInstance(activity).getMyDatabase().trackingDao().
                    updateTrackingDtoByArchive(id, true, false);
            return;
        }
        Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        OnOffLoadDto.OffLoadData offLoadData = new OnOffLoadDto.OffLoadData();
        offLoadData.isFinal = true;
        offLoadData.finalTrackNumber = trackNumber;
        for (int i = 0; i < onOffLoadDtos.size(); i++)
            offLoadData.offLoads.add(new OnOffLoadDto.OffLoad(onOffLoadDtos.get(i)));
        offLoadData.offLoadReports.addAll(offLoadReports);
        Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new OffLoadData(), new OffLoadDataIncomplete(), new UploadOnOffLoadError());
    }

    void thankYou(Activity activity) {
        activity.runOnUiThread(() ->
                new CustomToast().info(activity.getString(R.string.thank_you), Toast.LENGTH_LONG));
    }

    class OffLoadData implements ICallback<OnOffLoadDto.OffLoadResponses> {
        @Override
        public void execute(Response<OnOffLoadDto.OffLoadResponses> response) {
            if (response.body() != null && response.body().status == 200) {
                int state = response.body().isValid ? OffloadStateEnum.SENT.getValue() :
                        OffloadStateEnum.SENT_WITH_ERROR.getValue();
                MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase().onOffLoadDao().
                        updateOnOffLoad(state, response.body().targetObject);
                MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase().trackingDao().
                        updateTrackingDtoByArchive(id, true, false);
                MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase().offLoadReportDao().
                        updateOffLoadReportByIsSent(true);
                new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
//                new CustomDialog(DialogType.Green, getContext(), response.body().message,
//                        activity.getString(R.string.dear_user),
//                        activity.getString(R.string.upload_information),
//                        activity.getString(R.string.accepted));
            }
        }
    }
}

class Forbidden implements ICallback<ForbiddenDto.ForbiddenDtoResponses> {
    @Override
    public void execute(Response<ForbiddenDto.ForbiddenDtoResponses> response) {
        if (response.isSuccessful()) {
            MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase().forbiddenDao().
                    updateAllForbiddenDtoBySent(true);
            if (response.body() != null) {
                new CustomToast().success(MyApplication.getContext().
                                getString(R.string.report_forbid) + "\n" + response.body().message,
                        Toast.LENGTH_LONG);
            }
        }
    }
}

class ForbiddenIncomplete implements ICallbackIncomplete<ForbiddenDto.ForbiddenDtoResponses> {
    @Override
    public void executeIncomplete(Response<ForbiddenDto.ForbiddenDtoResponses> response) {
    }
}

class UploadForbidenError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
    }
}


class OffLoadDataIncomplete implements ICallbackIncomplete<OnOffLoadDto.OffLoadResponses> {
    @Override
    public void executeIncomplete(Response<OnOffLoadDto.OffLoadResponses> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
//        new CustomDialog(DialogType.Yellow, getContext(), error,
//                activity.getString(R.string.dear_user),
//                activity.getString(R.string.upload_information),
//                activity.getString(R.string.accepted));
    }
}

class UploadOnOffLoadError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
        String error = customErrorHandlingNew.getErrorMessageTotal(t);
        new CustomToast().error(error, Toast.LENGTH_LONG);
//        new CustomDialog(DialogType.Red, getContext(), error,
//                activity.getString(R.string.dear_user),
//                activity.getString(R.string.upload),
//                activity.getString(R.string.accepted));
    }
}
