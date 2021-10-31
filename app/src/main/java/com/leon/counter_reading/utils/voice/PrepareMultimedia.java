package com.leon.counter_reading.utils.voice;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.MultimediaUploadResponse;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.tables.VoiceGrouped;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.utils.CustomToast;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareMultimedia extends AsyncTask<Activity, Activity, Activity> {
    private final CustomProgressModel customProgressModel;
    private final Voice voice;
    private final String uuid;
    private final int position;
    private final VoiceGrouped voiceGrouped;

    public PrepareMultimedia(Activity activity, Voice voice, String description, String uuid, int position) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
        this.voice = voice;
        this.voice.Description = description;
        this.uuid = uuid;
        this.position = position;
        voiceGrouped = new VoiceGrouped();
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        voice.File = CustomFile.prepareVoiceToSend(voice.address);
        voiceGrouped.File.clear();
        voiceGrouped.File.add(voice.File);
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        super.onPostExecute(activity);
        customProgressModel.getDialog().dismiss();
        uploadVoice(activity);
    }

    void uploadVoice(Activity activity) {
        voiceGrouped.OnOffLoadId = RequestBody.create(
                voice.OnOffLoadId, MediaType.parse("text/plain"));
        voiceGrouped.Description = RequestBody.create(
                voice.Description, MediaType.parse("text/plain"));
        Retrofit retrofit = MyApplication.getApplicationComponent().Retrofit();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<MultimediaUploadResponse> call = iAbfaService.fileUploadGrouped(
                voiceGrouped.File, voiceGrouped.OnOffLoadId, voiceGrouped.Description);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new UploadVoice(activity), new UploadVoiceIncomplete(activity), new uploadVoiceError(activity));
    }

    void saveVoice(boolean isSent) {
        voice.isSent = isSent;
        if (MyApplication.getApplicationComponent().MyDatabase()
                .imageDao().getImagesById(voice.id).size() > 0) {
            MyApplication.getApplicationComponent().MyDatabase().voiceDao().updateVoice(voice);
        } else {
            MyApplication.getApplicationComponent().MyDatabase().voiceDao().insertVoice(voice);
        }
    }

    void finishDescription(Activity activity, String message) {
        MyApplication.getApplicationComponent().MyDatabase()
                .onOffLoadDao().updateOnOffLoadDescription(uuid, message);
        Intent intent = new Intent();
        intent.putExtra(BundleEnum.POSITION.getValue(), position);
        intent.putExtra(BundleEnum.BILL_ID.getValue(), uuid);
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }

    class UploadVoice implements ICallback<MultimediaUploadResponse> {
        private final Activity activity;

        public UploadVoice(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void execute(Response<MultimediaUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
            } else {
                new CustomToast().warning(activity.getString(R.string.error_upload), Toast.LENGTH_LONG);
            }
            saveVoice(response.body() != null && response.body().status == 200);
            finishDescription(activity, voice.Description);
        }
    }

    class UploadVoiceIncomplete implements ICallbackIncomplete<MultimediaUploadResponse> {
        private final Activity activity;

        public UploadVoiceIncomplete(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeIncomplete(Response<MultimediaUploadResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomToast().warning(error, Toast.LENGTH_LONG);
            saveVoice(false);
            finishDescription(activity, voice.Description);
        }
    }

    class uploadVoiceError implements ICallbackError {
        private final Activity activity;

        public uploadVoiceError(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomToast().error(error, Toast.LENGTH_LONG);
            saveVoice(false);
            finishDescription(activity, voice.Description);
        }
    }
}