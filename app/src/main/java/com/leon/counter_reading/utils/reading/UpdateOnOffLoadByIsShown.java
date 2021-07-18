package com.leon.counter_reading.utils.reading;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.MyDatabaseClient;

import static com.leon.counter_reading.MyApplication.readingData;

public class UpdateOnOffLoadByIsShown extends AsyncTask<Activity, Void, Void> {
    int position;

    public UpdateOnOffLoadByIsShown(int position) {
        super();
        this.position = position;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        readingData.onOffLoadDtos.get(position).counterNumberShown = true;
        ((ReadingActivity)(activities[0])).setupViewPagerAdapter(position);
        MyDatabase myDatabase = MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase();
        myDatabase.onOffLoadDao().updateOnOffLoad(readingData.onOffLoadDtos.get(position));
        return null;
    }
}