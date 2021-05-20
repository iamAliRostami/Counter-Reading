package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentUploadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
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
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadFragment extends Fragment {
    int[] imageSrc = {
            R.drawable.img_upload_on,
            R.drawable.img_upload_off,
            R.drawable.img_multimedia};
    int type;
    ArrayList<String> json;
    FragmentUploadBinding binding;
    ArrayList<Image> images = new ArrayList<>();
    ArrayList<Voice> voice = new ArrayList<>();
    Image.ImageMultiple imageMultiples = new Image.ImageMultiple();
    Voice.VoiceMultiple voiceMultiples = new Voice.VoiceMultiple();
    Activity activity;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
    ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();
    ISharedPreferenceManager sharedPreferenceManager;

    public UploadFragment() {
    }

    public static UploadFragment newInstance(int type, ArrayList<TrackingDto> trackingDtos) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putInt(BundleEnum.TYPE.getValue(), type);

        Gson gson = new Gson();
        ArrayList<String> json = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            trackingDtos.forEach(trackingDto -> json.add(gson.toJson(trackingDto)));
        else
            for (TrackingDto trackingDto : trackingDtos)
                json.add(gson.toJson(trackingDto));
        args.putStringArrayList(BundleEnum.TRACKING.getValue(), json);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(BundleEnum.TYPE.getValue());
            json = getArguments().getStringArrayList(
                    BundleEnum.TRACKING.getValue());

        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        activity = getActivity();
        sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        if (type == 3) {
            binding.linearLayoutSpinner.setVisibility(View.GONE);
        } else {
            trackingDtos.clear();
            items.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                json.forEach(s -> {
                    Gson gson = new Gson();
                    trackingDtos.add(gson.fromJson(s, TrackingDto.class));
                    items.add(String.valueOf(trackingDtos.get(trackingDtos.size() - 1).trackNumber));
                });
            } else
                for (String s : json) {
                    Gson gson = new Gson();
                    trackingDtos.add(gson.fromJson(s, TrackingDto.class));
                    items.add(String.valueOf(trackingDtos.get(trackingDtos.size() - 1).trackNumber));
                }
            items.add(0, getString(R.string.select_one));
            setupSpinner();
        }
        binding.imageViewUpload.setImageResource(imageSrc[type - 1]);
        setOnButtonUploadClickListener();
    }

    void setupSpinner() {
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(spinnerCustomAdapter);
    }

    boolean checkOnOffLoad() {
        int total, mane = 0, unread, alalPercent;
        MyDatabase myDatabase = MyDatabaseClient.getInstance(activity).getMyDatabase();
        if (binding.spinner.getSelectedItemPosition() != 0) {
            total = myDatabase.onOffLoadDao().getOnOffLoadCount(
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id);
            unread = myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id);
            ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().
                    getCounterStateDtosIsMane(true));
            for (int i = 0; i < isManes.size(); i++) {
                mane = mane + myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i),
                        trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id);
            }

            alalPercent = myDatabase.trackingDao().getAlalHesabByZoneId(
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId);
        } else return false;
        double alalMane = (double) mane / total * 100;
        if (unread > 0) {
            new CustomToast().info("همکار گرامی!\nتعداد " + unread + " اشتراک قرائت نشده است.", Toast.LENGTH_LONG);
            return false;
        } else if (mane > 0 && alalMane > (double) alalPercent) {
            new CustomToast().info("همکار گرامی!\nدرصد علی الحساب بالاتر از حد مجاز است.", Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }

    void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
            if (type == 1 || type == 2) {
                if (checkOnOffLoad())
                    new prepareOffLoadToUpload().execute();
            } else if (type == 3) {
                new prepareMultimediaToUpload().execute();
            }
        });
    }

    void thankYou() {
        activity.runOnUiThread(() -> new CustomToast().info(getString(R.string.thank_you), Toast.LENGTH_LONG));
    }

    void updateImages() {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).isSent = true;
            MyDatabaseClient.getInstance(getContext()).getMyDatabase().imageDao()
                    .updateImage(images.get(i));
        }
    }

    void updateVoice() {
        for (int i = 0; i < voice.size(); i++) {
            voice.get(i).isSent = true;
            MyDatabaseClient.getInstance(getContext()).getMyDatabase().voiceDao()
                    .updateVoice(voice.get(i));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewUpload.setImageDrawable(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        images = null;
        imageMultiples = null;
        imageSrc = null;
        trackingDtos = null;
        onOffLoadDtos = null;
        items = null;
    }

    @SuppressLint("StaticFieldLeak")
    class prepareOffLoadToUpload extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public prepareOffLoadToUpload() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            forbiddenDtos.clear();
            forbiddenDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    forbiddenDao().getAllForbiddenDto(false));
            onOffLoadDtos.clear();
            onOffLoadDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(
//                    "12126666",
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id,
                    OffloadStateEnum.INSERTED.getValue()));
            offLoadReports.clear();
            offLoadReports.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    offLoadReportDao().getAllOffLoadReport());
            return null;
        }

        @Override
        protected void onPreExecute() {
            customProgressBar = new CustomProgressBar();
            customProgressBar.show(activity, false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            customProgressBar.getDialog().dismiss();
            uploadOffLoad();
            if (forbiddenDtos.size() > 0) {
                uploadForbid();
            }
            super.onPostExecute(integer);
        }

        void uploadForbid() {
            ForbiddenDto.ForbiddenDtoRequestMultiple forbiddenDtoRequestMultiple =
                    new ForbiddenDto.ForbiddenDtoRequestMultiple();
            Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
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
                    new Forbidden(), new ForbiddenIncomplete(), new Error());
        }

        void uploadOffLoad() {
            if (onOffLoadDtos.size() <= 0) {
                thankYou();
                onOffLoadDtos.clear();
                onOffLoadDtos.add(MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(
                        trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id));
            }
            if (onOffLoadDtos.size() == 0 || onOffLoadDtos.get(0) == null) {
                MyDatabaseClient.getInstance(activity).getMyDatabase().trackingDao().
                        updateTrackingDtoByArchive(trackingDtos.get(
                                binding.spinner.getSelectedItemPosition() - 1).id, true, false);
                return;
            }
            Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
            IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            OnOffLoadDto.OffLoadData offLoadData = new OnOffLoadDto.OffLoadData();
            offLoadData.isFinal = true;
            offLoadData.finalTrackNumber = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber;
            for (int i = 0; i < onOffLoadDtos.size(); i++)
                offLoadData.offLoads.add(new OnOffLoadDto.OffLoad(onOffLoadDtos.get(i)));
            offLoadData.offLoadReports.addAll(offLoadReports);
            Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                    new offLoadData(), new offLoadDataIncomplete(), new uploadError());

        }
    }

    @SuppressLint("StaticFieldLeak")
    class prepareMultimediaToUpload extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public prepareMultimediaToUpload() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            images.clear();
            images.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao()
                    .getImagesByBySent(false));
            voice.clear();
            voice.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().voiceDao().
                    getVoicesByBySent(false));
            for (int i = 0; i < images.size(); i++) {
                Bitmap bitmap = CustomFile.loadImage(activity, images.get(i).address);
                if (bitmap != null) {
                    images.get(i).File = CustomFile.bitmapToFile(
                            CustomFile.loadImage(activity, images.get(i).address), activity);
                    imageMultiples.OnOffLoadId.add(RequestBody.create(images.get(i).OnOffLoadId,
                            MediaType.parse("text/plain")));
                    imageMultiples.Description.add(RequestBody.create(images.get(i).Description,
                            MediaType.parse("text/plain")));
                    imageMultiples.File.add(images.get(i).File);
                } else {
                    MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().
                            deleteImage(images.get(i).id);
                }
            }
            for (int i = 0; i < voice.size(); i++) {
                voice.get(i).File = CustomFile.prepareVoiceToSend(voice.get(i).address);
                if (voice.get(i).File != null) {
                    voiceMultiples.OnOffLoadId.add(RequestBody.create(voice.get(i).OnOffLoadId,
                            MediaType.parse("text/plain")));
                    voiceMultiples.Description.add(RequestBody.create(voice.get(i).Description,
                            MediaType.parse("text/plain")));
                    voiceMultiples.File.add(voice.get(i).File);
                } else {
                    MyDatabaseClient.getInstance(activity).getMyDatabase().voiceDao().
                            deleteVoice(voice.get(i).id);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            customProgressBar = new CustomProgressBar();
            customProgressBar.show(activity, false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            customProgressBar.getDialog().dismiss();
            uploadMultimedia();
            uploadVoice();
            super.onPostExecute(integer);
        }

        void uploadVoice() {
            if (voice.size() > 0) {
                Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
                IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
                Call<Voice.VoiceUploadResponse> call = iAbfaService.voiceUploadMultiple(
                        voiceMultiples.File, voiceMultiples.OnOffLoadId, voiceMultiples.Description);
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                        new uploadVoice(), new uploadVoiceIncomplete(), new uploadError());
            } else {
                activity.runOnUiThread(() -> new CustomToast().info(getString(R.string.there_is_no_message), Toast.LENGTH_LONG));
            }
        }

        void uploadMultimedia() {
            if (images.size() > 0) {
                Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
                IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
                Call<Image.ImageUploadResponse> call = iAbfaService.fileUploadMultiple(
                        imageMultiples.File, imageMultiples.OnOffLoadId, imageMultiples.Description);
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                        new uploadMultimedia(), new uploadMultimediaIncomplete(), new uploadError());
            } else {
                activity.runOnUiThread(() -> new CustomToast().info(getString(R.string.there_is_no_images), Toast.LENGTH_LONG));
            }
        }
    }

    class uploadMultimedia implements ICallback<Image.ImageUploadResponse> {
        @Override
        public void execute(Response<Image.ImageUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message);
                updateImages();
            }
        }
    }

    class uploadMultimediaIncomplete implements ICallbackIncomplete<Image.ImageUploadResponse> {
        @Override
        public void executeIncomplete(Response<Image.ImageUploadResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, getContext(), error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.upload_multimedia),
                    activity.getString(R.string.accepted));
        }
    }

    class uploadVoice implements ICallback<Voice.VoiceUploadResponse> {
        @Override
        public void execute(Response<Voice.VoiceUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message);
                updateVoice();
            }
        }
    }

    class uploadVoiceIncomplete implements ICallbackIncomplete<Voice.VoiceUploadResponse> {
        @Override
        public void executeIncomplete(Response<Voice.VoiceUploadResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, getContext(), error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.upload_message),
                    activity.getString(R.string.accepted));
        }
    }

    class Forbidden implements ICallback<ForbiddenDto.ForbiddenDtoResponses> {
        @Override
        public void execute(Response<ForbiddenDto.ForbiddenDtoResponses> response) {
            if (response.isSuccessful()) {
                MyDatabaseClient.getInstance(activity).getMyDatabase().forbiddenDao().
                        updateAllForbiddenDtoBySent(true);
                if (response.body() != null) {
                    new CustomToast().success(getString(R.string.report_forbid) + "\n" +
                            response.body().message, Toast.LENGTH_LONG);
                }
            }
        }
    }

    static class ForbiddenIncomplete implements ICallbackIncomplete<ForbiddenDto.ForbiddenDtoResponses> {
        @Override
        public void executeIncomplete(Response<ForbiddenDto.ForbiddenDtoResponses> response) {
        }
    }

    static class Error implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
        }
    }

    class offLoadData implements ICallback<OnOffLoadDto.OffLoadResponses> {
        @Override
        public void execute(Response<OnOffLoadDto.OffLoadResponses> response) {
            if (response.body() != null && response.body().status == 200) {
                int state = response.body().isValid ? OffloadStateEnum.SENT.getValue() :
                        OffloadStateEnum.SENT_WITH_ERROR.getValue();
                for (int i = 0; i < response.body().targetObject.size(); i++) {
                    MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                            updateOnOffLoad(state, response.body().targetObject.get(i));
                }
                MyDatabaseClient.getInstance(activity).getMyDatabase().trackingDao().
                        updateTrackingDtoByArchive(trackingDtos.get(
                                binding.spinner.getSelectedItemPosition() - 1).id, true, false);
                MyDatabaseClient.getInstance(activity).getMyDatabase().offLoadReportDao().
                        deleteAllOffLoadReport();
                new CustomDialog(DialogType.Green, getContext(), response.body().message,
                        activity.getString(R.string.dear_user),
                        activity.getString(R.string.upload_information),
                        activity.getString(R.string.accepted));
            }
        }
    }

    class offLoadDataIncomplete implements ICallbackIncomplete<OnOffLoadDto.OffLoadResponses> {
        @Override
        public void executeIncomplete(Response<OnOffLoadDto.OffLoadResponses> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, getContext(), error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.upload_information),
                    activity.getString(R.string.accepted));
        }
    }

    class uploadError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.Red, getContext(), error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.upload),
                    activity.getString(R.string.accepted));
        }
    }
}