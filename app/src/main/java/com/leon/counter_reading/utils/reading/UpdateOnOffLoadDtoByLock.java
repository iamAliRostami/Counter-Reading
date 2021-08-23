package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.readingData;
import static com.leon.counter_reading.MyApplication.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.activities.ReadingActivity;

public class UpdateOnOffLoadDtoByLock extends AsyncTask<Activity, Void, Void> {
    int position, trackNumber;
    String id;

    public UpdateOnOffLoadDtoByLock(int position, int trackNumber, String id) {
        super();
        this.position = position;
        this.trackNumber = trackNumber;
        this.id = id;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        for (int i = 0; i < readingDataTemp.onOffLoadDtos.size(); i++) {
            if (readingDataTemp.onOffLoadDtos.get(i).id.equals(id))
                readingDataTemp.onOffLoadDtos.get(i).isLocked = true;
        }

        MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoadByLock(id, trackNumber, true);
        readingData.onOffLoadDtos.get(position).isLocked = true;
        ((ReadingActivity) (activities[0])).setupViewPagerAdapter(position);
        return null;
    }
}