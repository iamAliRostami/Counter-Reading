package com.leon.counter_reading.utils.uploading;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomProgressBar;

import java.util.ArrayList;

public class GetUploadDBData extends AsyncTask<Activity, Integer, Integer> {
    private final CustomProgressBar customProgressBar;

    public GetUploadDBData(Activity activity) {
        super();

        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        customProgressBar.getDialog().dismiss();
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
