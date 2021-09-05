package com.leon.counter_reading.utils.downloading;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Download extends AsyncTask<Activity, Void, Void> {
    private final CustomProgressBar customProgressBar;

    public Download(Activity activity) {
        super();
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<ReadingData> call = iAbfaService.loadData(BuildConfig.VERSION_CODE);
        activities[0].runOnUiThread(() ->
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activities[0],
                        new DownloadCompleted(activities[0]), new DownloadIncomplete(), new DownloadError()));
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void unused) {
        customProgressBar.getDialog().dismiss();
        super.onPostExecute(unused);
    }
}

class DownloadCompleted implements ICallback<ReadingData> {
    Activity activity;

    public DownloadCompleted(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(Response<ReadingData> response) {
        if (response != null && response.body() != null) {
            ReadingData readingData = response.body();
            ReadingData readingDataTemp = response.body();
            MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
            ArrayList<TrackingDto> trackingDtos = new ArrayList<>(myDatabase.trackingDao()
                    .getTrackingDtoNotArchive(false));
            final boolean[] isActive = {false};

            for (int i = 0, trackingDtosSize = trackingDtos.size(); i < trackingDtosSize; i++) {
                TrackingDto trackingDtoI = trackingDtos.get(i);
                for (int j = 0, dtosSize = readingDataTemp.trackingDtos.size(); j < dtosSize; j++) {
                    TrackingDto trackingDtoJ = readingDataTemp.trackingDtos.get(j);
                    if (trackingDtoI.id.equals(trackingDtoJ.id) ||
                            trackingDtoI.trackNumber == trackingDtoJ.trackNumber) {
                        readingData.trackingDtos.remove(trackingDtoJ);
                        if (trackingDtoI.isActive)
                            isActive[0] = true;
                    }
                }
            }
            if (readingData.trackingDtos.size() > 0) {
                if (!isActive[0])
                    readingData.trackingDtos.get(0).isActive = true;
                myDatabase.trackingDao().insertAllTrackingDtos(readingData.trackingDtos);
            }
//            long startTime = Calendar.getInstance().getTimeInMillis();
//            long endTime = Calendar.getInstance().getTimeInMillis();
//            Log.e("start", String.valueOf(startTime));
//            Log.e("end", String.valueOf(endTime));
//            Log.e("length", String.valueOf(endTime - startTime));
            ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>(
                    myDatabase.counterStateDao().getCounterStateDtos());
            for (int j = 0, counterStateDtosSize = counterStateDtos.size(); j < counterStateDtosSize; j++) {
                CounterStateDto counterStateDto = counterStateDtos.get(j);
                for (int i = 0; i < readingDataTemp.counterStateDtos.size(); i++) {
                    if (counterStateDto.id == readingDataTemp.counterStateDtos.get(i).id)
                        readingData.counterStateDtos.remove(
                                readingDataTemp.counterStateDtos.get(i));
                }
            }
            myDatabase.counterStateDao().insertAllCounterStateDto(readingData.counterStateDtos);

            ArrayList<KarbariDto> karbariDtos = new ArrayList<>(
                    myDatabase.karbariDao().getAllKarbariDto());
            for (int j = 0, karbariDtosSize = karbariDtos.size(); j < karbariDtosSize; j++) {
                KarbariDto karbariDto = karbariDtos.get(j);
                for (int i = 0; i < readingDataTemp.karbariDtos.size(); i++) {
                    if (karbariDto.id == readingDataTemp.karbariDtos.get(i).id)
                        readingData.karbariDtos.remove(readingDataTemp.karbariDtos.get(i));
                }
            }
            myDatabase.karbariDao().insertAllKarbariDtos(readingData.karbariDtos);

            ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>(
                    myDatabase.qotrDictionaryDao().getAllQotrDictionaries());
            for (int j = 0, qotrDictionariesSize = qotrDictionaries.size(); j < qotrDictionariesSize; j++) {
                QotrDictionary qotrDictionary = qotrDictionaries.get(j);
                for (int i = 0; i < readingDataTemp.qotrDictionary.size(); i++) {
                    if (qotrDictionary.id == readingDataTemp.qotrDictionary.get(i).id)
                        readingData.qotrDictionary.remove(readingDataTemp.qotrDictionary.get(i));
                }
            }
            myDatabase.qotrDictionaryDao().insertQotrDictionaries(readingData.qotrDictionary);

            ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>(
                    myDatabase.readingConfigDefaultDao().getReadingConfigDefaultDtos());
            for (int j = 0, readingConfigDefaultDtosSize = readingConfigDefaultDtos.size();
                 j < readingConfigDefaultDtosSize; j++) {
                ReadingConfigDefaultDto readingConfigDefaultDto = readingConfigDefaultDtos.get(j);
                for (int i = 0; i < readingDataTemp.readingConfigDefaultDtos.size(); i++) {
                    if (readingConfigDefaultDto.id.equals(
                            readingDataTemp.readingConfigDefaultDtos.get(i).id)) readingData.
                            readingConfigDefaultDtos.remove(readingDataTemp.
                            readingConfigDefaultDtos.get(i));
                }
            }
            myDatabase.readingConfigDefaultDao().insertAllReadingConfigDefault(
                    readingData.readingConfigDefaultDtos);

            ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>(
                    myDatabase.onOffLoadDao().getAllOnOffLoad());
            for (int j = 0, onOffLoadDtosSize = onOffLoadDtos.size(); j < onOffLoadDtosSize; j++) {
                OnOffLoadDto onOffLoadDto = onOffLoadDtos.get(j);
                for (int i = 0; i < readingDataTemp.onOffLoadDtos.size(); i++) {
                    if (onOffLoadDto.id.equals(readingDataTemp.onOffLoadDtos.get(i).id) &&
                            onOffLoadDto.trackingId.equals(readingDataTemp.onOffLoadDtos.get(i).trackingId)
                    ) {
                        readingData.onOffLoadDtos.remove(readingDataTemp.onOffLoadDtos.get(i));
                    }
                }
            }
            myDatabase.onOffLoadDao().insertAllOnOffLoad(readingData.onOffLoadDtos);

            if (readingData.counterReportDtos.size() > 0) {
                myDatabase.counterReportDao().deleteAllCounterReport();
                myDatabase.counterReportDao().insertAllCounterStateReport(
                        readingData.counterReportDtos);
            }
            String message = String.format(MyApplication.getContext().getString(R.string.download_message),
                    readingData.trackingDtos.size(), readingData.onOffLoadDtos.size());
            activity.runOnUiThread(() -> new CustomDialogModel(DialogType.Green,
                    activity, message,
                    MyApplication.getContext().getString(R.string.dear_user),
                    MyApplication.getContext().getString(R.string.download),
                    MyApplication.getContext().getString(R.string.accepted)));
        }
    }
}

class DownloadIncomplete implements ICallbackIncomplete<ReadingData> {
    @Override
    public void executeIncomplete(Response<ReadingData> response) {
        CustomErrorHandling customErrorHandling = new CustomErrorHandling(MyApplication.getContext());
        String error = customErrorHandling.getErrorMessageDefault(response);
        if (response.code() == 400) {
            CustomErrorHandling.APIError apiError = customErrorHandling.parseError(response);
            error = apiError.message();
        }
        new CustomToast().warning(error, Toast.LENGTH_LONG);
    }
}

class DownloadError implements ICallbackError {
    @Override
    public void executeError(Throwable t) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
        String error = customErrorHandlingNew.getErrorMessageTotal(t);
        new CustomToast().error(error, Toast.LENGTH_LONG);
    }
}