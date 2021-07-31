package com.leon.counter_reading.utils.voice;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
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
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class PrepareMultimedia extends AsyncTask<Activity, Activity, Activity> {
    private final CustomProgressBar customProgressBar;
    private final Voice voice;
    private final String uuid;
    private final int position;
    private final Voice.VoiceGrouped voiceGrouped;

    public PrepareMultimedia(Activity activity, Voice voice, String description, String uuid, int position) {
        super();
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
        this.voice = voice;
        this.voice.Description = description;
        this.uuid = uuid;
        this.position = position;
        voiceGrouped = new Voice.VoiceGrouped();
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        voice.File = CustomFile.prepareVoiceToSend(voice.address);
        voiceGrouped.File.clear();
        voiceGrouped.File.add(voice.File);
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
        uploadVoice(activity);
    }

    void uploadVoice(Activity activity) {
        voiceGrouped.OnOffLoadId = RequestBody.create(
                voice.OnOffLoadId, MediaType.parse("text/plain"));
        voiceGrouped.Description = RequestBody.create(
                voice.Description, MediaType.parse("text/plain"));
        ISharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(
                sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<Image.ImageUploadResponse> call = iAbfaService.fileUploadGrouped(
                voiceGrouped.File, voiceGrouped.OnOffLoadId, voiceGrouped.Description);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new UploadVoice(activity), new UploadVoiceIncomplete(activity), new uploadVoiceError(activity));
    }

    void saveVoice(Activity activity, boolean isSent) {
        voice.isSent = isSent;
        if (MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao()
                .getImagesById(voice.id).size() > 0) {
            MyDatabaseClient.getInstance(activity).getMyDatabase().voiceDao().updateVoice(voice);
        } else {
            MyDatabaseClient.getInstance(activity).getMyDatabase().voiceDao().insertVoice(voice);
        }
    }

    void finishDescription(Activity activity, String message) {
        MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                updateOnOffLoadDescription(uuid, message);
        Intent intent = new Intent();
        intent.putExtra(BundleEnum.POSITION.getValue(), position);
        intent.putExtra(BundleEnum.BILL_ID.getValue(), uuid);
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }

    class UploadVoice implements ICallback<Image.ImageUploadResponse> {
        Activity activity;

        public UploadVoice(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void execute(Response<Image.ImageUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
            } else {
                new CustomToast().warning(activity.getString(R.string.error_upload), Toast.LENGTH_LONG);
            }
            saveVoice(activity, response.body() != null && response.body().status == 200);
            finishDescription(activity, voice.Description);
        }
    }

    class UploadVoiceIncomplete implements ICallbackIncomplete<Image.ImageUploadResponse> {
        Activity activity;

        public UploadVoiceIncomplete(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeIncomplete(Response<Image.ImageUploadResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomToast().warning(error, Toast.LENGTH_LONG);
            saveVoice(activity, false);
            finishDescription(activity, voice.Description);
        }
    }

    class uploadVoiceError implements ICallbackError {
        Activity activity;

        public uploadVoiceError(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomToast().error(error, Toast.LENGTH_LONG);
            saveVoice(activity, false);
            finishDescription(activity, voice.Description);
        }
    }
}