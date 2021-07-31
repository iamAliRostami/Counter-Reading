package com.leon.counter_reading.utils.reporting;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.ReadingReportActivity;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.util.ArrayList;

public class GetReadingReportDBData extends AsyncTask<Activity, Integer, Integer> {
    CustomProgressBar customProgressBar;
    private final String uuid;
    private final int trackNumber;

    public GetReadingReportDBData(Activity activity, int trackNumber, String uuid) {
        super();
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
        this.trackNumber = trackNumber;
        this.uuid = uuid;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        customProgressBar.getDialog().dismiss();
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        ArrayList<CounterReportDto> counterReportDtos = new ArrayList<>(MyDatabaseClient.getInstance(activities[0]).
                getMyDatabase().counterReportDao().getAllCounterStateReport());
        ArrayList<OffLoadReport> offLoadReports = new ArrayList<>(MyDatabaseClient.getInstance(activities[0]).getMyDatabase().
                offLoadReportDao().getAllOffLoadReportById(uuid, trackNumber));
        for (int i = 0; i < offLoadReports.size(); i++) {
            for (int j = 0; j < counterReportDtos.size(); j++) {
                if (offLoadReports.get(i).reportId == counterReportDtos.get(j).id) {
                    counterReportDtos.get(j).isSelected = true;
                }
            }
        }
        ((ReadingReportActivity) (activities[0])).setupRecyclerView(counterReportDtos, offLoadReports);
        return null;
    }
}