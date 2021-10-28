package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.readingData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;

import com.leon.counter_reading.helpers.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Update extends AsyncTask<Activity, Void, Void> {
    private final int position;

    @SuppressLint("SimpleDateFormat")
    public Update(int position, Location location) {
        super();
        this.position = position;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS");
        //Parsing the given String to Date object
//        Date date;
        if (location != null) {
            readingData.onOffLoadDtos.get(position).x = location.getLongitude();
            readingData.onOffLoadDtos.get(position).y = location.getLatitude();
            readingData.onOffLoadDtos.get(position).gisAccuracy = location.getAccuracy();
            readingData.onOffLoadDtos.get(position).locationDateTime = dateFormatter.format(new Date(location.getTime()));
//            Log.e("time 1", String.valueOf(location.getTime()));
//            Log.e("time 2", );
        }
        readingData.onOffLoadDtos.get(position).phoneDateTime = dateFormatter.format(new Date(Calendar.getInstance().getTimeInMillis()));
//        date = new Date(Calendar.getInstance().getTimeInMillis());
//        Log.e("time 3", String.valueOf(Calendar.getInstance().getTimeInMillis()));
//        Log.e("time 4", dateFormatter.format(date));
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        MyApplication.getApplicationComponent().MyDatabase()
                .onOffLoadDao().updateOnOffLoad(readingData.onOffLoadDtos.get(position));
        return null;
    }
}
