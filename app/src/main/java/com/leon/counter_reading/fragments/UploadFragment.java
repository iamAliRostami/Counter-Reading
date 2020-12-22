package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentUploadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.Image;
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
    private final int[] imageSrc = {R.drawable.img_upload_on, R.drawable.img_upload_off, R.drawable.img_multimedia};
    int type;
    FragmentUploadBinding binding;
    ArrayList<Image> images = new ArrayList<>();
    Image.ImageMultiple imageMultiples = new Image.ImageMultiple();
    Activity context;

    public UploadFragment() {
    }

    public static UploadFragment newInstance(int type) {
        UploadFragment fragment = new UploadFragment();
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
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        context = getActivity();
        binding.imageViewUpload.setImageResource(imageSrc[type - 1]);
        setOnButtonUploadClickListener();
    }

    void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
            if (type == 3) {
                new prepareMultimediaToUpload().execute();
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    class prepareMultimediaToUpload extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public prepareMultimediaToUpload() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            images.addAll(MyDatabaseClient.getInstance(context).getMyDatabase().imageDao()
                    .getImagesByBySent(false));
            for (int i = 0; i < images.size(); i++) {
                images.get(i).File = CustomFile.bitmapToFile(
                        CustomFile.loadImage(context, images.get(i).address), context);
                imageMultiples.OnOffLoadId.add(RequestBody.create(images.get(i).OnOffLoadId,
                        MediaType.parse("text/plain")));
                imageMultiples.Description.add(RequestBody.create(images.get(i).Description,
                        MediaType.parse("text/plain")));
                imageMultiples.File.add(images.get(i).File);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            customProgressBar = new CustomProgressBar();
            context.runOnUiThread(() -> customProgressBar.show(context, false));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            context.runOnUiThread(() -> customProgressBar.getDialog().dismiss());
            uploadMultimedia();
            super.onPostExecute(integer);
        }

        void uploadMultimedia() {
            if (images.size() > 0) {
                Retrofit retrofit = NetworkHelper.getInstance();
                IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
                Call<Image.ImageUploadResponse> call = iAbfaService.fileUploadMultiple(
                        imageMultiples.File, imageMultiples.OnOffLoadId, imageMultiples.Description);
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                        new uploadMultimedia(), new uploadMultimediaIncomplete(), new uploadError());
            } else {
                CustomToast customToast = new CustomToast();
                context.runOnUiThread(() -> customToast.info(getString(R.string.there_is_no_images)));
            }
        }
    }

    class uploadMultimedia implements ICallback<Image.ImageUploadResponse> {
        @Override
        public void execute(Response<Image.ImageUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                CustomToast customToast = new CustomToast();
                customToast.success(response.body().message);
                saveImages();
            }
        }
    }

    class uploadMultimediaIncomplete implements ICallbackIncomplete<Image.ImageUploadResponse> {
        @Override
        public void executeIncomplete(Response<Image.ImageUploadResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, getContext(), error,
                    context.getString(R.string.dear_user),
                    context.getString(R.string.upload_multimedia),
                    context.getString(R.string.accepted));
        }
    }

    class uploadError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(context);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.Red, getContext(), error,
                    context.getString(R.string.dear_user),
                    context.getString(R.string.upload_multimedia),
                    context.getString(R.string.accepted));
        }
    }

    void saveImages() {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).isSent = true;
            MyDatabaseClient.getInstance(getContext()).getMyDatabase().imageDao()
                    .updateImage(images.get(i));
        }
        images.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewUpload.setImageDrawable(null);
    }
}