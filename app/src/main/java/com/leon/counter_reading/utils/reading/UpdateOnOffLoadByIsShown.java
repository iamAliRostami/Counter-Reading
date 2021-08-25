package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.readingData;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;

public class UpdateOnOffLoadByIsShown extends AsyncTask<Activity, Void, Void> {
    private final int position;

    public UpdateOnOffLoadByIsShown(int position) {
        super();
        this.position = position;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        readingData.onOffLoadDtos.get(position).counterNumberShown = true;
        MyApplication.getApplicationComponent().MyDatabase()
                .onOffLoadDao().updateOnOffLoad(readingData.onOffLoadDtos.get(position));
//        ((ReadingActivity)(activities[0])).setupViewPagerAdapter(position);
        return null;
    }
}