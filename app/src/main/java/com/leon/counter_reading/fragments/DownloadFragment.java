package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDownloadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownloadFragment extends Fragment {
    final int[] imageSrc = {R.drawable.img_download, R.drawable.img_download_retry,
            R.drawable.img_download_off, R.drawable.img_download_special};
    FragmentDownloadBinding binding;
    int type;
    Context context;

    public DownloadFragment() {
    }

    public static DownloadFragment newInstance(int type) {
        DownloadFragment fragment = new DownloadFragment();
        Bundle args = new Bundle();
        args.putInt(BundleEnum.TYPE.getValue(), type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(BundleEnum.TYPE.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        context = getActivity();
        binding.imageViewDownload.setImageResource(imageSrc[type - 1]);
        setOnButtonDownloadClickListener();
    }

    void setOnButtonDownloadClickListener() {
        binding.buttonDownload.setOnClickListener(v -> downloadType());
    }

    void downloadType() {
        ISharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context, SharedReferenceNames.ACCOUNT.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<ReadingData> call = iAbfaService.loadData();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new DownloadCompleted(), new DownloadIncomplete(), new DownloadError());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDownload.setImageDrawable(null);
    }

    @SuppressLint("DefaultLocale")
    class DownloadCompleted implements ICallback<ReadingData> {
        @Override
        public void execute(Response<ReadingData> response) {
            //TODO
            if (response != null && response.body() != null) {
                ReadingData readingData = response.body();
                ReadingData readingDataTemp = response.body();
                MyDatabase myDatabase = MyDatabaseClient.getInstance(context).getMyDatabase();
                ArrayList<TrackingDto> trackingDtos = new ArrayList<>(
                        myDatabase.trackingDao().getTrackingDtoNotArchive(false));
                final boolean[] isActive = {false};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    trackingDtos.forEach(trackingDto -> {
                        for (int i = 0; i < readingDataTemp.trackingDtos.size(); i++) {
                            if (trackingDto.id.equals(readingDataTemp.trackingDtos.get(i).id) || trackingDto.trackNumber == readingDataTemp.trackingDtos.get(i).trackNumber)
                                readingData.trackingDtos.remove(readingDataTemp.trackingDtos.get(i));
                            if (trackingDto.isActive)
                                isActive[0] = true;
                        }
                    });
                } else {
                    for (TrackingDto trackingDto : trackingDtos) {
                        for (TrackingDto trackingDto1 : readingDataTemp.trackingDtos) {
                            if (trackingDto.id.equals(trackingDto1.id) || trackingDto.trackNumber == trackingDto1.trackNumber) {
                                myDatabase.trackingDao().updateTrackingDtoByArchive(trackingDto.id, false, false);
                                readingData.trackingDtos.remove(trackingDto1);
                            }
                        }
                        if (trackingDto.isActive)
                            isActive[0] = true;
                    }
                }
                if (readingData.trackingDtos.size() > 0) {
                    if (!isActive[0])
                        readingData.trackingDtos.get(0).isActive = true;
                    myDatabase.trackingDao().insertAllTrackingDtos(readingData.trackingDtos);
                }
                ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>(
                        myDatabase.counterStateDao().getCounterStateDtos());
                for (CounterStateDto counterStateDto : counterStateDtos)
                    for (int i = 0; i < readingDataTemp.counterStateDtos.size(); i++) {
                        if (counterStateDto.id == readingDataTemp.counterStateDtos.get(i).id)
                            readingData.counterStateDtos.remove(
                                    readingDataTemp.counterStateDtos.get(i));
                    }
                myDatabase.counterStateDao().insertAllCounterStateDto(readingData.counterStateDtos);

                ArrayList<KarbariDto> karbariDtos = new ArrayList<>(
                        myDatabase.karbariDao().getAllKarbariDto());
                for (KarbariDto karbariDto : karbariDtos)
                    for (int i = 0; i < readingDataTemp.karbariDtos.size(); i++) {
                        if (karbariDto.id == readingDataTemp.karbariDtos.get(i).id)
                            readingData.karbariDtos.remove(readingDataTemp.karbariDtos.get(i));
                    }
                myDatabase.karbariDao().insertAllKarbariDtos(readingData.karbariDtos);

                ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>(
                        myDatabase.qotrDictionaryDao().getAllQotrDictionaries());
                for (QotrDictionary qotrDictionary : qotrDictionaries)
                    for (int i = 0; i < readingDataTemp.qotrDictionary.size(); i++) {
                        if (qotrDictionary.id == readingDataTemp.qotrDictionary.get(i).id)
                            readingData.qotrDictionary.remove(readingDataTemp.qotrDictionary.get(i));
                    }
                myDatabase.qotrDictionaryDao().insertQotrDictionaries(readingData.qotrDictionary);

                ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>(
                        myDatabase.readingConfigDefaultDao().getReadingConfigDefaultDtos());
                for (ReadingConfigDefaultDto readingConfigDefaultDto : readingConfigDefaultDtos)
                    for (int i = 0; i < readingDataTemp.readingConfigDefaultDtos.size(); i++) {
                        if (readingConfigDefaultDto.id.equals(
                                readingDataTemp.readingConfigDefaultDtos.get(i).id)) readingData.
                                readingConfigDefaultDtos.remove(readingDataTemp.
                                readingConfigDefaultDtos.get(i));
                    }
                myDatabase.readingConfigDefaultDao().insertAllReadingConfigDefault(
                        readingData.readingConfigDefaultDtos);

                ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>(
                        myDatabase.onOffLoadDao().getAllOnOffLoad());
                for (OnOffLoadDto onOffLoadDto : onOffLoadDtos)
                    for (int i = 0; i < readingDataTemp.onOffLoadDtos.size(); i++) {
                        if (onOffLoadDto.id.equals(readingDataTemp.onOffLoadDtos.get(i).id) &&
                                onOffLoadDto.trackingId.equals(readingDataTemp.onOffLoadDtos.get(i).trackingId)
                        ) {
                            readingData.onOffLoadDtos.remove(readingDataTemp.onOffLoadDtos.get(i));
                        }
                    }
                myDatabase.onOffLoadDao().insertAllOnOffLoad(readingData.onOffLoadDtos);
                ArrayList<CounterReportDto> counterReportDtos = new ArrayList<>(
                        myDatabase.counterReportDao().getAllCounterStateReport());
                for (CounterReportDto counterReportDto : counterReportDtos) {
                    for (int i = 0; i < readingDataTemp.counterReportDtos.size(); i++) {
                        if (counterReportDto.id == readingDataTemp.counterReportDtos.get(i).id)
                            readingData.counterReportDtos.remove(
                                    readingDataTemp.counterReportDtos.get(i));
                    }
                }
                myDatabase.counterReportDao().insertAllCounterStateReport(
                        readingData.counterReportDtos);
                String message = String.format("تعداد %d مسیر و %d اشتراک بارگیری شد.", readingData.trackingDtos.size(), readingData.onOffLoadDtos.size());
                new CustomDialog(DialogType.Green, context, message,
                        context.getString(R.string.dear_user),
                        context.getString(R.string.download),
                        context.getString(R.string.accepted));
            }
        }
    }

    class DownloadIncomplete implements ICallbackIncomplete<ReadingData> {
        @Override
        public void executeIncomplete(Response<ReadingData> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, context, error,
                    context.getString(R.string.dear_user),
                    context.getString(R.string.download),
                    context.getString(R.string.accepted));
        }
    }

    class DownloadError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomToast().error(error);
        }
    }

}