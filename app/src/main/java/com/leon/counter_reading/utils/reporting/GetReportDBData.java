package com.leon.counter_reading.utils.reporting;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.activities.ReportActivity;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.utils.MyDatabase;

import java.util.ArrayList;

public class GetReportDBData extends AsyncTask<Activity, Integer, Integer> {
    private final CustomProgressModel customProgressModel;
    private final MyDatabase myDatabase;
    private final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private int zero, normal, high, low, unread, total, isMane;

    public GetReportDBData(Activity activity) {
        super();
        myDatabase = MyApplication.getApplicationComponent().MyDatabase();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        customProgressModel.getDialog().dismiss();
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        trackingDtos.addAll(myDatabase.trackingDao().getTrackingDtosIsActiveNotArchive(true, false));
        ArrayList<Integer> isManes = new ArrayList<>();
        if (trackingDtos.size() > 0)
            isManes.addAll(myDatabase.counterStateDao().getCounterStateDtosIsMane(true, trackingDtos.get(0).zoneId));
        for (int j = 0, trackingDtosSize = trackingDtos.size(); j < trackingDtosSize; j++) {
            TrackingDto trackingDto = trackingDtos.get(j);
            for (int i = 0; i < isManes.size(); i++) {
                isMane += myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i),
                        trackingDto.trackNumber);
            }
            zero += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(
                    trackingDto.trackNumber, HighLowStateEnum.ZERO.getValue());
            high += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(
                    trackingDto.trackNumber, HighLowStateEnum.HIGH.getValue());
            low += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(
                    trackingDto.trackNumber, HighLowStateEnum.LOW.getValue());
            normal += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(
                    trackingDto.trackNumber, HighLowStateEnum.NORMAL.getValue());
            unread += myDatabase.onOffLoadDao().getOnOffLoadReadCount(0, trackingDto.trackNumber);
            total += myDatabase.onOffLoadDao().getOnOffLoadCount(trackingDto.trackNumber);
        }
        if (trackingDtos.size() > 0)
            counterStateDtos.addAll(myDatabase.counterStateDao().getCounterStateDtos(trackingDtos.get(0).zoneId));

        activities[0].runOnUiThread(() -> ((ReportActivity) (activities[0])).
                setupViewPager(counterStateDtos, trackingDtos,
                        zero, normal, high, low, total, isMane, unread));
        return null;
    }
}