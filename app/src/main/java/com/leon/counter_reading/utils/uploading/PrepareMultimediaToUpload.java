package com.leon.counter_reading.utils.uploading;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.di.view_model.NetworkHelper;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareMultimediaToUpload extends AsyncTask<Activity, Activity, Activity> {
    CustomProgressBar customProgressBar;
    ArrayList<Image> images = new ArrayList<>();
    ArrayList<Voice> voice = new ArrayList<>();
    Image.ImageMultiple imageMultiples = new Image.ImageMultiple();
    Voice.VoiceMultiple voiceMultiples = new Voice.VoiceMultiple();
    ISharedPreferenceManager sharedPreferenceManager;

    public PrepareMultimediaToUpload(Activity activity) {
        super();
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
        sharedPreferenceManager = MyApplication.getApplicationComponent().SharedPreferenceModel();
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        images.clear();
        images.addAll(MyApplication.getApplicationComponent().MyDatabase().imageDao()
                .getImagesByBySent(false));
        voice.clear();
        voice.addAll(MyApplication.getApplicationComponent().MyDatabase().voiceDao().
                getVoicesByBySent(false));
        for (int i = 0; i < images.size(); i++) {
            Bitmap bitmap = CustomFile.loadImage(activities[0], images.get(i).address);
            if (bitmap != null) {
                images.get(i).File = CustomFile.bitmapToFile(
                        CustomFile.loadImage(activities[0], images.get(i).address), activities[0]);
                imageMultiples.OnOffLoadId.add(RequestBody.create(images.get(i).OnOffLoadId,
                        MediaType.parse("text/plain")));
                imageMultiples.Description.add(RequestBody.create(images.get(i).Description,
                        MediaType.parse("text/plain")));
                imageMultiples.File.add(images.get(i).File);
            } else {
                MyApplication.getApplicationComponent().MyDatabase().imageDao().
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
                MyApplication.getApplicationComponent().MyDatabase().voiceDao().
                        deleteVoice(voice.get(i).id);
            }
        }
        return activities[0];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        customProgressBar.getDialog().dismiss();
        uploadImages(activity);
        uploadVoice(activity);
    }

    void uploadVoice(Activity activity) {
        if (voice.size() > 0) {
            Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.
                    getStringData(SharedReferenceKeys.TOKEN.getValue()));
            IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            Call<Voice.VoiceUploadResponse> call = iAbfaService.voiceUploadMultiple(
                    voiceMultiples.File, voiceMultiples.OnOffLoadId, voiceMultiples.Description);
            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                    new UploadVoices(), new UploadVoicesIncomplete(), new UploadMultimediaError());
        } else {
            activity.runOnUiThread(() ->
                    new CustomToast().info(activity.getString(R.string.there_is_no_message),
                            Toast.LENGTH_LONG));
        }
    }

    void uploadImages(Activity activity) {
        if (images.size() > 0) {
            Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
            IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            Call<Image.ImageUploadResponse> call = iAbfaService.fileUploadMultiple(
                    imageMultiples.File, imageMultiples.OnOffLoadId, imageMultiples.Description);
            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                    new UploadImages(), new UploadImagesIncomplete(), new UploadMultimediaError());
        } else {
            activity.runOnUiThread(() ->
                    new CustomToast().info(activity.getString(R.string.there_is_no_images),
                            Toast.LENGTH_LONG));
        }
    }

    class UploadImages implements ICallback<Image.ImageUploadResponse> {
        @Override
        public void execute(Response<Image.ImageUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
                updateImages();
            }
        }

        void updateImages() {
            for (int i = 0; i < images.size(); i++) {
                images.get(i).isSent = true;
                MyApplication.getApplicationComponent().MyDatabase().imageDao()
                        .updateImage(images.get(i));
            }
        }
    }

    class UploadVoices implements ICallback<Voice.VoiceUploadResponse> {
        @Override
        public void execute(Response<Voice.VoiceUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message);
                updateVoice();
            }
        }

        void updateVoice() {
            for (int i = 0; i < voice.size(); i++) {
                voice.get(i).isSent = true;
                MyApplication.getApplicationComponent().MyDatabase().voiceDao()
                        .updateVoice(voice.get(i));
            }
        }
    }
}

class UploadImagesIncomplete implements ICallbackIncomplete<Image.ImageUploadResponse> {
    @Override
    public void executeIncomplete(Response<Image.ImageUploadResponse> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);

//        new CustomDialog(DialogType.Yellow, getContext(), error,
//                activity.getString(R.string.dear_user),
//                activity.getString(R.string.upload_multimedia),
//                activity.getString(R.string.accepted));
    }
}

class UploadVoicesIncomplete implements ICallbackIncomplete<Voice.VoiceUploadResponse> {
    @Override
    public void executeIncomplete(Response<Voice.VoiceUploadResponse> response) {
        CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(MyApplication.getContext());
        String error = customErrorHandlingNew.getErrorMessageDefault(response);
        new CustomToast().warning(error, Toast.LENGTH_LONG);
//        new CustomDialog(DialogType.Yellow, getContext(), error,
//                activity.getString(R.string.dear_user),
//                activity.getString(R.string.upload_message),
//                activity.getString(R.string.accepted));
    }
}

class UploadMultimediaError implements ICallbackError {
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

