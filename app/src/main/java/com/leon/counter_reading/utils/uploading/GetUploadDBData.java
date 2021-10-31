package com.leon.counter_reading.utils.uploading;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.di.view_model.CustomProgressModel;

import java.util.ArrayList;

public class GetUploadDBData extends AsyncTask<Activity, Integer, Integer> {
    private final CustomProgressModel customProgressModel;

    public GetUploadDBData(Activity activity) {
        super();

        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        customProgressModel.getDialog().dismiss();
        super.onPostExecute(integer);
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        ArrayList<TrackingDto> trackingDtos = new ArrayList<>(MyApplication.getApplicationComponent().MyDatabase().
                trackingDao().getTrackingDtoNotArchive(false));
        ((UploadActivity) (activities[0])).setupUI(trackingDtos);
        return null;
    }
}
