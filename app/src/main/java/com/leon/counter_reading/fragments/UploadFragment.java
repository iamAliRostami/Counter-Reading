package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentUploadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;

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
    FragmentUploadBinding binding;
    ArrayList<Image> images = new ArrayList<>();
    Image.ImageMultiple imageMultiples = new Image.ImageMultiple();
    Activity activity;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
    ArrayList<ForbiddenDto> forbiddenDtos = new ArrayList<>();

    public UploadFragment() {
    }

    public static UploadFragment newInstance(int type, ArrayList<TrackingDto> trackingDtos) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putInt(BundleEnum.TYPE.getValue(), type);

        Gson gson = new Gson();
        ArrayList<String> json = new ArrayList<>();
        for (TrackingDto trackingDto : trackingDtos) {
            String jsonTemp = gson.toJson(trackingDto);
            json.add(jsonTemp);
        }
        args.putStringArrayList(BundleEnum.TRACKING.getValue(), json);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(BundleEnum.TYPE.getValue());
            Gson gson = new Gson();
            ArrayList<String> json = getArguments().getStringArrayList(
                    BundleEnum.TRACKING.getValue());
            for (String s : json) {
                trackingDtos.add(gson.fromJson(s, TrackingDto.class));
                items.add(trackingDtos.get(trackingDtos.size() - 1).zoneTitle);
            }
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
        if (type == 3) {
            binding.linearLayoutSpinner.setVisibility(View.GONE);
        }
        activity = getActivity();
        binding.imageViewUpload.setImageResource(imageSrc[type - 1]);
        setOnButtonUploadClickListener();
        setupSpinner();
    }

    void setupSpinner() {
        items.add(0, getString(R.string.all_items));
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(spinnerCustomAdapter);
    }

    void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
            if (type == 1) {
                new prepareOffLoadToUpload().execute();
            } else if (type == 3) {
                new prepareMultimediaToUpload().execute();
            }
        });
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
            if (binding.spinner.getSelectedItemPosition() == 0) {
                onOffLoadDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(true,
                        OffloadStateEnum.INSERTED.getValue()));
            } else {
                onOffLoadDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadReadByTrackingAndOffLoad(true,
                        onOffLoadDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackingId,
                        OffloadStateEnum.INSERTED.getValue()));
            }
            offLoadReports.clear();
            for (int i = 0; i < onOffLoadDtos.size(); i++) {
                offLoadReports.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                        offLoadReportDao().getAllOffLoadReportById(onOffLoadDtos.get(i).id));
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
            if (onOffLoadDtos.size() > 0 || forbiddenDtos.size() > 0) {
                uploadOffLoad();
                uploadForbid();
            } else {
                activity.runOnUiThread(() -> new CustomToast().info(getString(R.string.there_is_no_offload)));
            }
            super.onPostExecute(integer);
        }

        void uploadForbid() {
//TODO
        }

        void uploadOffLoad() {
            if (onOffLoadDtos.size() > 0) {
                Retrofit retrofit = NetworkHelper.getInstance();
                IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
                OnOffLoadDto.OffLoadData offLoadData = new OnOffLoadDto.OffLoadData();
                offLoadData.isFinal = true;
                for (int i = 0; i < onOffLoadDtos.size(); i++)
                    offLoadData.offLoads.add(new OnOffLoadDto.OffLoad(onOffLoadDtos.get(i)));
                offLoadData.offLoadReports.addAll(offLoadReports);
                Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                        new offLoadData(), new offLoadDataIncomplete(), new uploadError());
            }
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
            super.onPostExecute(integer);
        }

        void uploadMultimedia() {
            if (images.size() > 0) {
                Retrofit retrofit = NetworkHelper.getInstance();
                IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
                Call<Image.ImageUploadResponse> call = iAbfaService.fileUploadMultiple(
                        imageMultiples.File, imageMultiples.OnOffLoadId, imageMultiples.Description);
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                        new uploadMultimedia(), new uploadMultimediaIncomplete(), new uploadError());
            } else {
                CustomToast customToast = new CustomToast();
                activity.runOnUiThread(() -> customToast.info(getString(R.string.there_is_no_images)));
            }
        }
    }

    class uploadMultimedia implements ICallback<Image.ImageUploadResponse> {
        @Override
        public void execute(Response<Image.ImageUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                CustomToast customToast = new CustomToast();
                customToast.success(response.body().message);
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

    void updateImages() {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).isSent = true;
            MyDatabaseClient.getInstance(getContext()).getMyDatabase().imageDao()
                    .updateImage(images.get(i));
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
}