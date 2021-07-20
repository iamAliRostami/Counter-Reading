package com.leon.counter_reading.utils.reporting;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import com.leon.counter_reading.activities.ReportActivity;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.util.ArrayList;

public class GetReportDBData extends AsyncTask<Activity, Integer, Integer> {
    CustomProgressBar customProgressBar;
    MyDatabase myDatabase;
    int zero, normal, high, low, unread, total, isMane;
    ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();

    public GetReportDBData(Activity activity) {
        super();
        myDatabase = MyDatabaseClient.getInstance(activity).getMyDatabase();
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        customProgressBar.getDialog().dismiss();
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        trackingDtos.addAll(myDatabase.trackingDao().getTrackingDtosIsActiveNotArchive(true, false));
        counterStateDtos.addAll(myDatabase.counterStateDao().getCounterStateDtos());
        ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().getCounterStateDtosIsMane(true));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            trackingDtos.forEach(trackingDto -> {
                isManes.forEach(integer ->
                        isMane += myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(integer,
                                trackingDto.trackNumber));
                zero += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(
                        trackingDto.trackNumber, HighLowStateEnum.ZERO.getValue());
                high += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(
                        trackingDto.trackNumber, HighLowStateEnum.HIGH.getValue());
                low += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(
                        trackingDto.trackNumber, HighLowStateEnum.LOW.getValue());
                normal += myDatabase.onOffLoadDao().getOnOffLoadReadCountByStatus(
                        trackingDto.trackNumber, HighLowStateEnum.NORMAL.getValue());
                unread += myDatabase.onOffLoadDao().getOnOffLoadReadCount(0,
                        trackingDto.trackNumber);
                total += myDatabase.
                        onOffLoadDao().getOnOffLoadCount(trackingDto.trackNumber);
            });
        } else
            for (TrackingDto trackingDto : trackingDtos) {
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
        activities[0].runOnUiThread(() -> ((ReportActivity) (activities[0])).
                setupViewPager(counterStateDtos, trackingDtos,
                        zero, normal, high, low, total, isMane, unread));
//        runOnUiThread(ReportActivity.this::setupViewPager);
        return null;
    }
}