package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.readingData;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.utils.LocationTracker;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.MyDatabaseClient;

public class Update extends AsyncTask<Activity, Void, Void> {
    int position;

    public Update(int position, LocationTracker locationTracker) {
        super();
        this.position = position;
        readingData.onOffLoadDtos.get(position).x = locationTracker.getLongitude();
        readingData.onOffLoadDtos.get(position).y = locationTracker.getLatitude();
        readingData.onOffLoadDtos.get(position).gisAccuracy = locationTracker.getAccuracy();
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        MyDatabase myDatabase = MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase();
        myDatabase.onOffLoadDao().updateOnOffLoad(readingData.onOffLoadDtos.get(position));
        return null;
    }
}
