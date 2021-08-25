package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.readingData;
import static com.leon.counter_reading.MyApplication.readingDataTemp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.tables.OnOffLoadDto;

public class Result extends AsyncTask<Activity, Void, Void> {
    private final Intent data;

    public Result(Intent data) {
        super();
        this.data = data;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        int position = data.getExtras().getInt(BundleEnum.POSITION.getValue()), i = 0;
        String uuid = data.getExtras().getString(BundleEnum.BILL_ID.getValue());
        MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(true, uuid);
        readingData.onOffLoadDtos.set(position, MyApplication.getApplicationComponent().MyDatabase()
                .onOffLoadDao().getAllOnOffLoadById(uuid, readingData.onOffLoadDtos.get(position).trackNumber));
        ((ReadingActivity) (activities[0])).setupViewPagerAdapter(position);
        for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
            if (onOffLoadDto.id.equals(uuid))
                readingDataTemp.onOffLoadDtos.set(i, readingData.onOffLoadDtos.get(position));
            i++;
        }
        return null;
    }
}