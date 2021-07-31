package com.leon.counter_reading.utils.uploading;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.util.ArrayList;

public class GetUploadDBData extends AsyncTask<Activity, Integer, Integer> {
    CustomProgressBar customProgressBar;
    private ArrayList<TrackingDto> trackingDtos;

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
        trackingDtos = new ArrayList<>(MyDatabaseClient.getInstance(activities[0]).getMyDatabase().
                trackingDao().getTrackingDtoNotArchive(false));
        ((UploadActivity) (activities[0])).setupUI(trackingDtos);
        return null;
    }
}
