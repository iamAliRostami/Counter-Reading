package com.leon.counter_reading.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ReadingReportCustomAdapter;
import com.leon.counter_reading.databinding.ActivityReadingReportBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
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
    int position;

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
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
        }
        new GetDBData().execute();
        binding.buttonSubmit.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(BundleEnum.POSITION.getValue(), position);
            intent.putExtra(BundleEnum.BILL_ID.getValue(), uuid);
            setResult(RESULT_OK, intent);
            finish();
        });
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
            readingReportCustomAdapter = new ReadingReportCustomAdapter(activity, uuid, position,
                    counterReportDtos, offLoadReports);
            binding.listViewReports.setAdapter(readingReportCustomAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        new CustomToast().warning(getString(R.string.submit_for_back));
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
        Debug.getNativeHeapAllocatedSize();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }
}