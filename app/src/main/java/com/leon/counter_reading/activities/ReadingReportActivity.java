package com.leon.counter_reading.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.adapters.ReadingReportCustomAdapter;
import com.leon.counter_reading.databinding.ActivityReadingReportBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import java.util.ArrayList;

public class ReadingReportActivity extends AppCompatActivity {

    ActivityReadingReportBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;
    ArrayList<CounterReportDto> counterReportDtos;
    ArrayList<OffLoadReport> offLoadReports;
    ReadingReportCustomAdapter readingReportCustomAdapter;
    Activity activity;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityReadingReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        initialize();
    }

    void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
        }
        new GetDBData().execute();
    }

    @SuppressLint("StaticFieldLeak")
    class GetDBData extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public GetDBData() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressBar = new CustomProgressBar();
            customProgressBar.show(activity, false);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            customProgressBar.getDialog().dismiss();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            counterReportDtos = new ArrayList<>(MyDatabaseClient.getInstance(activity).
                    getMyDatabase().counterReportDao().getAllCounterStateReport());
            offLoadReports = new ArrayList<>(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    offLoadReportDao().getAllOffLoadReportById(uuid));

            for (int i = 0; i < offLoadReports.size(); i++) {
                for (int j = 0; j < counterReportDtos.size(); j++) {
                    if (offLoadReports.get(i).reportId == counterReportDtos.get(j).id) {
                        counterReportDtos.get(j).isSelected = true;
                    }
                }
            }
            activity.runOnUiThread(this::setupRecyclerView);
            return null;
        }

        void setupRecyclerView() {
            readingReportCustomAdapter = new ReadingReportCustomAdapter(activity, uuid,
                    counterReportDtos, offLoadReports);
            binding.listViewReports.setAdapter(readingReportCustomAdapter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        counterReportDtos = null;
        offLoadReports = null;
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}