package com.leon.counter_reading.utils.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.ImageGrouped;
import com.leon.counter_reading.tables.MultimediaUploadResponse;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrepareMultimedia extends AsyncTask<Activity, Integer, Activity> {
    private final CustomProgressBar customProgressBar;
    private final ImageGrouped imageGrouped = new ImageGrouped();
    private final ArrayList<Image> images;
    private final String description;
    private final boolean result;
    private final int position;

    public PrepareMultimedia(Activity activity, int position, boolean result, String description,
                             ArrayList<Image> images) {
        super();
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
        this.description = description;
        this.position = position;
        this.result = result;
        this.images = images;
    }

    @Override
    protected Activity doInBackground(Activity... activities) {
        imageGrouped.File.clear();
        for (int i = 0; i < images.size(); i++) {
            images.get(i).Description = description;
            if (!images.get(i).isSent && images.get(i).File == null) {
                imageGrouped.File.add(CustomFile.bitmapToFile(images.get(i).bitmap, activities[0]));
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
        uploadImage(activity);
    }

    void uploadImage(Activity activity) {
        if (imageGrouped.File.size() > 0) {
            imageGrouped.OnOffLoadId = RequestBody.create(images.get(0).OnOffLoadId,
                    MediaType.parse("text/plain"));
            imageGrouped.Description = RequestBody.create(images.get(0).Description,
                    MediaType.parse("text/plain"));
            Retrofit retrofit = MyApplication.getApplicationComponent().NetworkHelperModel()
                    .getInstance(true,MyApplication.getApplicationComponent().SharedPreferenceModel()
                            .getStringData(SharedReferenceKeys.TOKEN.getValue()),10,25,10);
            IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            Call<MultimediaUploadResponse> call = iAbfaService.fileUploadGrouped(imageGrouped.File,
                    imageGrouped.OnOffLoadId, imageGrouped.Description);
            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW_CANCELABLE.getValue(), activity,
                    new UploadImages(activity), new UploadImagesIncomplete(activity),
                    new UploadImagesError(activity));
        } else if (result) {
            setResult(activity, true);
        } else {
            activity.runOnUiThread(() ->
                    new CustomToast().warning(activity.getString(R.string.there_is_no_images), Toast.LENGTH_LONG));
        }
    }

    void setResult(Activity activity, boolean result) {
        if (result) {
            Intent intent = new Intent();
            intent.putExtra(BundleEnum.POSITION.getValue(), position);
            activity.setResult(Activity.RESULT_OK, intent);
        }
        activity.finish();
    }

    void saveImages(boolean isSent, Activity activity) {
        for (int i = 0; i < images.size(); i++) {
            if (!images.get(i).isSent) {
                images.get(i).isSent = isSent;
                if (MyApplication.getApplicationComponent().MyDatabase().imageDao()
                        .getImagesById(images.get(i).id).size() > 0) {
                    MyApplication.getApplicationComponent().MyDatabase().imageDao()
                            .updateImage(images.get(i));
                } else {
                    String address = CustomFile.saveTempBitmap(images.get(i).bitmap, activity);
                    if (!address.equals(activity.getString(R.string.error_external_storage_is_not_writable))) {
                        images.get(i).address = address;
                        MyApplication.getApplicationComponent().MyDatabase().imageDao()
                                .insertImage(images.get(i));
                    }
                }
            }
        }
    }

    class UploadImages implements ICallback<MultimediaUploadResponse> {
        private final Activity activity;

        public UploadImages(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void execute(Response<MultimediaUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
            } else {
                new CustomToast().warning(activity.getString(R.string.error_upload), Toast.LENGTH_LONG);
            }
            saveImages(response.body() != null && response.body().status == 200, activity);
            setResult(activity, result);
        }
    }

    class UploadImagesIncomplete implements ICallbackIncomplete<MultimediaUploadResponse> {
        private final Activity activity;

        public UploadImagesIncomplete(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeIncomplete(Response<MultimediaUploadResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomToast().warning(error, Toast.LENGTH_LONG);
            saveImages(false, activity);
            setResult(activity, result);
        }
    }

    class UploadImagesError implements ICallbackError {
        private final Activity activity;

        public UploadImagesError(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomToast().error(error, Toast.LENGTH_LONG);
            saveImages(false, activity);
            setResult(activity, result);
        }
    }
}