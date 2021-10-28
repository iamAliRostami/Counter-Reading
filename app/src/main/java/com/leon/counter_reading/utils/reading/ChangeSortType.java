package com.leon.counter_reading.utils.reading;



import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.di.view_model.CustomProgressModel;

import java.util.Collections;

public class ChangeSortType extends AsyncTask<Activity, Void, Void> {
    private final boolean sortType;
    private final CustomProgressModel customProgressModel;

    public ChangeSortType(Activity activity, boolean sortType) {
        super();
        this.sortType = sortType;
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        customProgressModel.getDialog().dismiss();
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        if (sortType) {
            Collections.sort(readingData.onOffLoadDtos,
                    (o1, o2) -> o2.eshterak.compareTo(o1.eshterak));
            Collections.sort(readingDataTemp.onOffLoadDtos,
                    (o1, o2) -> o2.eshterak.compareTo(o1.eshterak));
        } else {
            Collections.sort(readingData.onOffLoadDtos,
                    (o1, o2) -> o1.eshterak.compareTo(o2.eshterak));
            Collections.sort(readingDataTemp.onOffLoadDtos,
                    (o1, o2) -> o1.eshterak.compareTo(o2.eshterak));
        }
        ((ReadingActivity) (activities[0])).setupViewPagerAdapter(0);
        return null;
    }
}
