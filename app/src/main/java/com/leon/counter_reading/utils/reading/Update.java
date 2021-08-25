package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.readingData;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;

public class Update extends AsyncTask<Activity, Void, Void> {
    private final int position;

    public Update(int position, Location location) {
        super();
        this.position = position;
        readingData.onOffLoadDtos.get(position).x = location.getLongitude();
        readingData.onOffLoadDtos.get(position).y = location.getLatitude();
        readingData.onOffLoadDtos.get(position).gisAccuracy = location.getAccuracy();
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        MyApplication.getApplicationComponent().MyDatabase()
                .onOffLoadDao().updateOnOffLoad(readingData.onOffLoadDtos.get(position));
        return null;
    }
}
