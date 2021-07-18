package com.leon.counter_reading.utils.reading;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.MyDatabaseClient;

import static com.leon.counter_reading.MyApplication.readingData;
import static com.leon.counter_reading.MyApplication.readingDataTemp;

public class Result extends AsyncTask<Activity, Void, Void> {
    Intent data;

    public Result(Intent data) {
        super();
        this.data = data;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        int position = data.getExtras().getInt(BundleEnum.POSITION.getValue()), i = 0;
        String uuid = data.getExtras().getString(BundleEnum.BILL_ID.getValue());
        MyDatabase myDatabase = MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase();
        myDatabase.onOffLoadDao().updateOnOffLoad(true, uuid);
        readingData.onOffLoadDtos.set(position, myDatabase.onOffLoadDao().
                getAllOnOffLoadById(uuid, readingData.onOffLoadDtos.get(position).trackNumber));

        ((ReadingActivity)(activities[0])).setupViewPagerAdapter(position);
        for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
            if (onOffLoadDto.id.equals(uuid))
                readingDataTemp.onOffLoadDtos.set(i, readingData.onOffLoadDtos.get(position));
            i++;
        }
        return null;
    }
}