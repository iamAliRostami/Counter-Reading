package com.leon.counter_reading.utils.reading;


import static com.leon.counter_reading.MyApplication.readingData;
import static com.leon.counter_reading.MyApplication.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.utils.CustomProgressBar;

import java.util.Collections;

public class ChangeSortType extends AsyncTask<Activity, Void, Void> {
    boolean sortType;
    CustomProgressBar customProgressBar;

    public ChangeSortType(Activity activity, boolean sortType) {
        super();
        this.sortType = sortType;
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        customProgressBar.getDialog().dismiss();
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        if (sortType) {
            Collections.sort(readingData.onOffLoadDtos, (o1, o2) -> o2.eshterak.compareTo(
                    o1.eshterak));
            Collections.sort(readingDataTemp.onOffLoadDtos, (o1, o2) -> o2.eshterak.compareTo(
                    o1.eshterak));
        } else {
            Collections.sort(readingData.onOffLoadDtos, (o1, o2) -> o1.eshterak.compareTo(
                    o2.eshterak));
            Collections.sort(readingDataTemp.onOffLoadDtos, (o1, o2) -> o1.eshterak.compareTo(
                    o2.eshterak));
        }
        ((ReadingActivity) (activities[0])).setupViewPagerAdapter(0);
        return null;
    }
}
